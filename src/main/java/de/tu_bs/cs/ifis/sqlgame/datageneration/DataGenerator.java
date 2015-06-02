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
            int columns = this.calculateColumns(rel);
            this.calculatePrimaryKeyAssignments(rel);
            ArrayList<ArrayList<String>> dataList = new ArrayList<>();
            
            for (String dataConstraint : rel.getDataGeneration()) {
                int i;
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
                        dataList = this.generateDataFromFunction(true, null, null, columnFunctions, numberFunction, dataList);
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
        
    private int calculateColumns(Relation rel) {
        String tuple = rel.getDataGeneration().get(0);
        StringTokenizer st = new StringTokenizer(tuple, ";");
        return st.countTokens() - 1;
    }
    
    private ArrayList<ArrayList<String>> generateDataFromFunction(
            boolean refTypeNone,
            ArrayList<String> primaryKeyColumns,
            ArrayList<String> primaryKey,
            ArrayList<String> columnFunctions,
            String numberFunction,
            ArrayList<ArrayList<String>> dataList
    ) throws MySQLAlchemistException {
        int i = 0;
        int number = this.generateNumber(numberFunction);
        
        if (refTypeNone) {
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
                
                dataList.add(this.findAndExecuteFunction(number, functionName, params, this.checkPrimaryKeyStatus(i)));
                i++;
            }
        } else {
            ArrayList<String> values = new ArrayList<>();
            for (String columnString : primaryKeyColumns) {
                int column = Integer.parseInt(columnString);
                for (int j = 0; j < number; j++) {
                    values.add(primaryKey.get(i));
                }
                dataList.add(values);
                i++;
            }
        }
        
        return dataList;
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

    private String findAndExecuteFunction(String functionName, ArrayList<String> params) throws MySQLAlchemistException{
        String result = "";
        switch (functionName) {
            case("random"): {
                String para = params.get(0);
                switch (para) {
                    case("int"):{
                        result = generateInteger();
                        break;
                    }
                    case("double"):{
                        result = generateDouble();
                        break;
                    }
                    case("string"):{
                        int para2 = 5;
                        if(params.size() == 2){
                        para2 = Integer.parseInt(params.get(1));
                        }
                        result = generateString(para2);
                        break;
                    }
                    case("firstname"):{
                        result = generateFirstName();
                        break;
                    }
                    case("lastname"):{
                        result = generateLastName();
                        break;
                    }
                    case("fullname"):{
                        result = generateFullName();
                        break;
                    }
                    case("date"):{
                        result = generateDate();
                        break;
                    }
                    case("city"):{
                        result = generateCity();
                        break;
                    }
                    case("adress"):{
                        result = generateAdress();
                        break;
                    }
                    case("email"):{
                        result = generateEmail();
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
                        result = generateCustomData(para, random, def);
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
                        result = generateMinInteger(para2);
                        break;
                    }
                    case("double"):{
                        result = generateMinDouble(para2);
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
                        result = generateMaxInteger(para2);
                        break;
                    }
                    case("double"):{
                        result = generateMaxDouble(para2);
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
                result = generateGauss(para1, para2);
                break;
            }
            
            case("list"):{
                int para;
                if(params.size() == 1){
                para = Integer.parseInt(params.get(0));
                int tmp = para + 1;
                String tmp2 = "" + tmp;
                params.add(0, tmp2);
                params.remove(1);
                } else {
                    throw new MySQLAlchemistException("1 Parameter wird bei list benötigt", new Exception());
                }
                result = generateList(para);
                break;
            }
            
            case("fix"):{
                String para;
                if(params.size() == 1){
                para = params.get(0);
                } else {
                    throw new MySQLAlchemistException("1 Parameter wird bei fix benötigt", new Exception());
                }
                result = para;
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
    
    public String generateFirstName() {
        DataFactory df = new DataFactory();
        String result = "'" + df.getFirstName() + "'";
        return result;
    }
        
    public String generateLastName() {
        DataFactory df = new DataFactory();
        String result = "'" + df.getLastName() + "'";
        return result;
    }
    
    public String generateFullName() {
        DataFactory df = new DataFactory();
        String result = "'" + df.getFirstName() + " " + df.getLastName() + "'";
        return result;
    }
    
    public String generateInteger() {
        Random r = new Random();
        String result = "" + r.nextInt();
        return result;
    }
    
    public String generateDouble() {
        Random r = new Random();
        String result = "" + r.nextDouble();
        return result;
    }
    
    public String generateString(int length) {
        DataFactory df = new DataFactory();
        String result = df.getRandomChars(length);
        return result;
    }
    
    public String generateDate() {
        DataFactory df = new DataFactory();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(df.getBirthDate());
        String result = "'" + date + "'";
        return result;
    }
    
    public String generateCity() {
        DataFactory df = new DataFactory();
        String result = "'" + df.getCity() + "'";
        return result;
    }
    
    public String generateAdress() {
        DataFactory df = new DataFactory();
        String result = "'" + df.getAddress() + "'";
        return result;
    }
    
    public String generateEmail() {
        DataFactory df = new DataFactory();
        String result = "'" + df.getEmailAddress() + "'";
        return result;
    }
    
    public String generateMinInteger(int min) {
        DataFactory df = new DataFactory();
        String result = "" + df.getNumberBetween(min, 1000000);
        return result;
    }
    
    public String generateMinDouble(int min) {
        DataFactory df = new DataFactory();
        String result = "" + df.getNumberBetween(min, 1000000) + "." + df.getNumberBetween(0, 99);
        return result;
    }
        
    public String generateMaxInteger(int max) {
        DataFactory df = new DataFactory();
        String result = "" + df.getNumberUpTo(max);
        return result;
    }
    
    public String generateMaxDouble(int max) {
        DataFactory df = new DataFactory();
        String result = "" + df.getNumberUpTo(max) + "." + df.getNumberBetween(0, 99);
        return result;
    }
    
    public String generateGauss(double median, double sd) {
        Random r = new Random();
        double d =  median + r.nextGaussian() * sd;
        String result = "" + d;
        return result;
    }
    
    public String generateList(int start) {
        String result = "" + start;
        return result;
    }
    
    public String generateCustomData(String metaData, int random, String defaultValue) throws MySQLAlchemistException {
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
            String result;
                if(metaData.equals("size")){
                    result = df.getItem(valuesStringArray, random, defaultValue);
                } else{
                    result = "'" + df.getItem(valuesStringArray, random, defaultValue) + "'";
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
