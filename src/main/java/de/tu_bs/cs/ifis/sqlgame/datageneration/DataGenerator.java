/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_bs.cs.ifis.sqlgame.datageneration;

import org.fluttercode.datafactory.impl.DataFactory;
import java.io.*;
import com.typesafe.config.*;
import de.tu_bs.cs.ifis.sqlgame.dbconnection.DBConnection;
import de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.Relation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author Philips
 */
public class DataGenerator {
    
    private List<Relation> relations;
    private DBConnection dbConn;
    
    private int columns = 0;
    private ArrayList<ArrayList<String>> primaryKeyAssignments = new ArrayList<>();
    private ArrayList<ArrayList<String>> primaryKeyValues = new ArrayList<>();
    
    private final Config conf = ConfigFactory.load();
    
    public DataGenerator(List<Relation> relations, DBConnection dbConn) {
        this.relations = relations;
        this.dbConn = dbConn;
    }
    
    public void generateData() throws MySQLAlchemistException {
        for (Relation rel : this.relations) {
            if (rel.getDataGeneration().isEmpty()) {
                break;
            }
            this.calculateColumns(rel);
            this.calculatePrimaryKeyAssignments(rel);
            
            for (String dataConstraint : rel.getDataGeneration()) {
                int i;
                ArrayList<ArrayList<String>> dataList = new ArrayList<>();
                ArrayList<String> columnFunctions = new ArrayList<>();
                StringTokenizer st = new StringTokenizer(dataConstraint, ";");
                
                String numberFunction = st.nextToken();
                String refFunction = st.nextToken();
                while (st.hasMoreTokens()) {
                    columnFunctions.add(st.nextToken());
                }
                
                //Fill the list of primarykeys
                String primaryKeyColumns = "*";
                i = 0;
                for (String primaryKey : rel.getPrimaryKey()) {
                    if (i == 0) {
                        primaryKeyColumns = primaryKey;
                    } else {
                        primaryKeyColumns += primaryKey;
                    }
                }
                String selectStatement = "SELECT " + primaryKeyColumns + " FROM " + rel.getTableName();
                this.primaryKeyValues = this.dbConn.executeSQLSelectStatement(
                        this.conf.getString("auth.user"),
                        this.conf.getString("auth.pass"),
                        selectStatement
                );

                switch (refFunction) {
                    case "none": {
                        dataList = this.generateDataFromFunction(true, columnFunctions, numberFunction);
                        break;
                    }
                    
                    /*case "refAll": {
                        i = 0;
                        for (ArrayList<String> primaryKey : primaryKeys) {
                            if (i >= 0) {
                                dataList = this.generateDataFromFunction(true, primaryKeys.get(0), primaryKey, columnFunctions, numberFunction, dataList);
                            }
                            i++;
                        }
                        break;
                    }
                    
                    case "refRandom": {
                        Random rand = new Random();
                        int randomNum = rand.nextInt(primaryKeys.get(0).size());
                        dataList = this.generateDataFromFunction(true, primaryKeys.get(0), primaryKeys.get(randomNum), columnFunctions, numberFunction, dataList);
                    }*/
                }
                
                this.generateAndExecuteInsertStatements(rel.getTableName(), dataList);
            }
            
            //Reset the primarykey list
            this.primaryKeyValues = null;
        }
    }
    
    private void calculatePrimaryKeyAssignments(Relation rel) throws MySQLAlchemistException {
        ArrayList<String> primaryKeyAssignment;
        
        String selectStatement = "SELECT * FROM " + rel.getTableName();
        ArrayList<String> columnNames = this.dbConn.executeSQLSelectStatement(
                this.conf.getString("auth.user"),
                this.conf.getString("auth.pass"),
                selectStatement
        ).get(0);
        
        int i = 0;
        for (String columnName : columnNames) {
            if (rel.getPrimaryKey().contains(columnName.toLowerCase())) {
                primaryKeyAssignment = new ArrayList<>();
                primaryKeyAssignment.add(columnName);
                primaryKeyAssignment.add("" + i);
                this.primaryKeyAssignments.add(primaryKeyAssignment);
            }
            i++;
        }
    }
        
    private void calculateColumns(Relation rel) {
        String tuple = rel.getDataGeneration().get(0);
        StringTokenizer st = new StringTokenizer(tuple, ";");
        this.columns = st.countTokens() - 1;
    }
    
    private ArrayList<ArrayList<String>> generateDataFromFunction(boolean refTypeNone, ArrayList<String> columnFunctions, String numberFunction ) throws MySQLAlchemistException {
        int i;
        int number = this.generateNumber(numberFunction);
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        ArrayList<String> column = new ArrayList<>();
        ArrayList<Integer> primaryKeyColumnIndex = new ArrayList<>();
        for (i = 0; i < this.columns; i++) {
            for (ArrayList<String> primaryKeyAssignment : this.primaryKeyAssignments) {
                if (Integer.parseInt(primaryKeyAssignment.get(1)) == i) {
                    primaryKeyColumnIndex.add(i);
                }
            }
        }
        
        if (refTypeNone) {
            i = 0;
            ArrayList<ArrayList<ArrayList<String>>> primaryKeyFunctions = new ArrayList<>();
            for (String columnFunction : columnFunctions) {
                StringTokenizer st = new StringTokenizer(columnFunction, "$");
                String functionName = st.nextToken();

                ArrayList<String> params = new ArrayList<>();
                if (st.hasMoreTokens()) {
                    StringTokenizer stt = new StringTokenizer(st.nextToken(), ",");
                    while (stt.hasMoreTokens()) {
                        params.add(stt.nextToken());
                    }
                }
                if (!primaryKeyColumnIndex.contains(i)) {
                    for (int j = 0; j < number; j++) {
                        column.add(this.findAndExecuteFunction(functionName, params));
                    }
                    data.add(i, column);
                    column = new ArrayList<>();
                } else {
                    ArrayList<ArrayList<String>> primaryKeyFunction = new ArrayList<>();
                    ArrayList<String> functionNameList = new ArrayList<>();
                    functionNameList.add(functionName);
                    primaryKeyFunction.add(functionNameList);
                    primaryKeyFunction.add(params);
                    primaryKeyFunctions.add(primaryKeyFunction);
                }
                i++;
            }
            
            //Handle primary keys
            ArrayList<ArrayList<String>> primaryKeys = new ArrayList<>();
            for (i = 0; i < number; i++) {
                boolean primaryKeyExists = true;
                ArrayList<String> primaryKey = new ArrayList<>();
                while (!primaryKeyExists) {
                    for (int j = 0; j < this.primaryKeyValues.get(0).size(); j++) {
                        primaryKey.add(this.findAndExecuteFunction(primaryKeyFunctions.get(j).get(0).get(0), primaryKeyFunctions.get(j).get(1)));
                    }
                    if (!this.primaryKeyValues.contains(primaryKey)) {
                        primaryKeyExists = false;
                    }
                }
                primaryKeys.add(primaryKey);
            }
            
            for (i = 0; i < this.primaryKeyValues.get(0).size(); i++) {
                for (ArrayList<String> primaryKey : primaryKeys) {
                    column.add(primaryKey.get(i));
                }
                data.add(Integer.parseInt(this.primaryKeyAssignments.get(i).get(1)), column);
                column = new ArrayList<>();
            }
        }
        
        return data;
    }
    
    private boolean checkPrimaryKeyStatus(int columnIndex) {
        String columnIndexString = "" + columnIndex;
        for (ArrayList<String> primaryKeyAssignment : this.primaryKeyAssignments) {
            if (primaryKeyAssignment.get(1).equals(columnIndexString)) {
                return true;
            }
        }
        
        return false;
    }
    
    private int generateNumber(String numberFunction) throws MySQLAlchemistException {
        StringTokenizer st = new StringTokenizer(numberFunction, "$");
        String functionName = st.nextToken();
        
        int number = 5;
        if (st.hasMoreTokens()) {
            StringTokenizer stt = new StringTokenizer(st.nextToken(), ",");
            ArrayList<String> params = new ArrayList<>();
            while (stt.hasMoreTokens()) {
                params.add(stt.nextToken());
            }
            switch(functionName) {
                case "span": {
                    if (params.size() == 2) {
                        int param1 = Integer.parseInt(params.get(0));
                        int param2 = Integer.parseInt(params.get(1));
                        Random rand = new Random();
                        number = rand.nextInt(param2) + param1;
                    } else {
                        throw new MySQLAlchemistException("Es werden zwei Parameter bei der Spannen-Funktion benötigt.", new Exception());
                    }
                    break;
                }
            }
        } else {
            number = Integer.parseInt(functionName);
        }
        
        return number;
    }

    private ArrayList<String> findAndExecuteFunction(int quantity, String functionName, ArrayList<String> params, int column) throws MySQLAlchemistException{
        ArrayList<String> result = new ArrayList<>();
        switch (functionName) {
            case("random"): {
                String para = params.get(0);
                switch (para) {
                    case("int"):{
                        result = generateInteger(quantity);
                        break;
                    }
                    case("double"):{
                        result = generateDouble(quantity);
                        break;
                    }
                    case("string"):{
                        int para2 = 5;
                        if(params.size() == 2){
                        para2 = Integer.parseInt(params.get(1));
                        }
                        result = generateString(quantity, para2);
                        break;
                    }
                    case("firstname"):{
                        result = generateFirstName(quantity);
                        break;
                    }
                    case("lastname"):{
                        result = generateLastName(quantity);
                        break;
                    }
                    case("fullname"):{
                        result = generateFullName(quantity);
                        break;
                    }
                    case("date"):{
                        result = generateDate(quantity);
                        break;
                    }
                    case("city"):{
                        result = generateCity(quantity);
                        break;
                    }
                    case("adress"):{
                        result = generateAdress(quantity);
                        break;
                    }
                    case("email"):{
                        result = generateEmail(quantity);
                        break;
                    }
                    default:{
                        int random = 100;
                        String def = "NULL";
                        if(params.size() >= 2){
                            random = Integer.parseInt(params.get(1));
                            if(params.size() >= 3){
                                def = params.get(2); 
                            }
                        }
                        result = generateCustomData(para, quantity, random, def);
                        break;
                    }
                }
                break;
            }
            
            case("min"):{
                String para1;
                int para2;
                if(params.size() == 2){
                para1 = params.get(0);
                para2 = Integer.parseInt(params.get(1));
                } else {
                    throw new MySQLAlchemistException("2 Parameter werden bei min benötigt", new Exception());
                }
                switch(para1){
                    case("int"):{
                        result = generateMinInteger(quantity, para2);
                        break;
                    }
                    case("double"):{
                        result = generateMinDouble(quantity, para2);
                        break;
                    }
                }
                break;
            }
            
            case("max"):{
                String para1;
                int para2;
                if(params.size() == 2){
                para1 = params.get(0);
                para2 = Integer.parseInt(params.get(1));
                } else {
                    throw new MySQLAlchemistException("2 Parameter werden bei max benötigt", new Exception());
                }
                switch(para1){
                    case("int"):{
                        result = generateMaxInteger(quantity, para2);
                        break;
                    }
                    case("double"):{
                        result = generateMaxDouble(quantity, para2);
                        break;
                    }
                }
                break;
            }
            
            case("gauss"):{
                double para1;
                double para2;
                if(params.size() == 2){
                para1 = Double.parseDouble(params.get(0));
                para2 = Double.parseDouble(params.get(1));
                } else {
                    throw new MySQLAlchemistException("2 Parameter werden bei gauss benötigt", new Exception());
                }
                result = generateGauss(quantity, para1, para2);
                break;
            }
            
            case("list"):{
                int para;
                if(params.size() == 2){
                para = Integer.parseInt(params.get(0));
                } else {
                    throw new MySQLAlchemistException("1 Parameter wird bei list benötigt", new Exception());
                }
                result = generateList(quantity, para);
                break;
            }
        }
        return result;
    }
    
    private void generateAndExecuteInsertStatements(String tableName, ArrayList<ArrayList<String>> dataList) throws MySQLAlchemistException {
        ArrayList<String> insertStatements = new ArrayList<>();
        for (int i = 0; i < dataList.get(0).size(); i++) {
            String insertedValues = "";
            for (int j = 0; j < dataList.size(); j++) {
                if (j == 0) {
                    insertedValues += dataList.get(j).get(i);
                } else {
                    insertedValues += ", " + dataList.get(j).get(i);
                }
            }
            insertStatements.add("INSERT INTO " + tableName + " VALUES(" + insertedValues + ")");
        }
        this.dbConn.executeSQLUpdateStatement(
                this.conf.getString("auth.user"),
                this.conf.getString("auth.pass"),
                insertStatements
        );
    }
    
    public ArrayList<String> generateFirstName(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("'" + df.getFirstName() + "'");
        }
        return result;
    }
        
    public ArrayList<String> generateLastName(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("'" + df.getLastName() + "'");
        }
        return result;
    }
    
    public ArrayList<String> generateFullName(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("'" + df.getFirstName() + " " + df.getLastName() + "'");
        }
        return result;
    }
    
    public ArrayList<String> generateInteger(int quantity) {
        Random r = new Random();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("" + r.nextInt());
        }
        return result;
    }
    
    public ArrayList<String> generateDouble(int quantity) {
        Random r = new Random();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("" + r.nextDouble());
        }
        return result;
    }
    
    public ArrayList<String> generateString(int quantity, int length) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add(df.getRandomChars(length));
        }
        return result;
    }
    
    public ArrayList<String> generateDate(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            String date = new SimpleDateFormat("yyyy-MM-dd").format(df.getBirthDate());
            result.add("'" + date + "'");
        }
        return result;
    }
    
    public ArrayList<String> generateCity(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("'" + df.getCity() + "'");
        }
        return result;
    }
    
    public ArrayList<String> generateAdress(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("'" + df.getAddress() + "'");
        }
        return result;
    }
    
    public ArrayList<String> generateEmail(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("'" + df.getEmailAddress() + "'");
        }
        return result;
    }
    
    public ArrayList<String> generateMinInteger(int quantity, int para2) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("" + df.getNumberBetween(para2, 1000000));
        }
        return result;
    }
    
    public ArrayList<String> generateMinDouble(int quantity, int para2) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("" + df.getNumberBetween(para2, 1000000) + "." + df.getNumberBetween(0, 99));
        }
        return result;
    }
        
    public ArrayList<String> generateMaxInteger(int quantity, int para2) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("" + df.getNumberUpTo(para2));
        }
        return result;
    }
    
    public ArrayList<String> generateMaxDouble(int quantity, int para2) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("" + df.getNumberUpTo(para2) + "." + df.getNumberBetween(0, 99));
        }
        return result;
    }
    
    public ArrayList<String> generateGauss(int quantity, double median, double sd) {
        ArrayList<String> result = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < quantity; i++){
            double d =  median + r.nextGaussian() * sd;
            result.add("" + d);
        }
        return result;
    }
    
    public ArrayList<String> generateList(int quantity, int start) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = start; i < start + quantity; i++){
            result.add("" + i);
        }
        return result;
    }
    
    public ArrayList<String> generateCustomData(String metaData, int quantity, int random, String defaultValue) throws MySQLAlchemistException {
        try {
            DataFactory df = new DataFactory();
            Config conf = ConfigFactory.load();
            String path = conf.getString("input.dataGenPath");
            FileReader fr = new FileReader(path + metaData + ".txt");
            BufferedReader br = new BufferedReader(fr);
            String content = br.readLine();
            StringTokenizer st = new StringTokenizer(content, ";");
            ArrayList<String> values = new ArrayList();
            while (st.hasMoreTokens()) {
                values.add(st.nextToken());
            }
            String[] valuesStringArray = values.toArray(new String[values.size()]);
            ArrayList<String> result = new ArrayList<>();
            for (int i = 0; i < quantity; i++) {
                if(metaData.equals("size")){
                    result.add(df.getItem(valuesStringArray, random, defaultValue));
                } else{
                    result.add("'" + df.getItem(valuesStringArray, random, defaultValue) + "'");
                }
            }
            return result;
        } catch (IOException e) {
            throw new MySQLAlchemistException("Fehler beim Generieren", e);
        }
        
    }
}

/*
                i = 0;
                for (String columnValue : refTable.get(1)) {
                    ArrayList<String> primaryKey = new ArrayList<>();
                    if (i == 0) {
                        int j = 0;
                        for (String columnName : refTable.get(0)) {
                            if (rel.getPrimaryKey().contains(columnName.toLowerCase())) {
                                primaryKey.add("" + j);
                            }
                            j++;
                        }
                    }

                    if (i % columns == 0) {
                        primaryKeys.add(primaryKey);
                    } else {
                        if (rel.getPrimaryKey().contains(refTable.get(0).get(i % columns).toLowerCase())) {
                            primaryKey.add(columnValue);
                        }
                        i++;
                    }
                }
*/
