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
    private ArrayList<ArrayList<String>> referenceAssignments = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> referenceValues = new ArrayList<>();
    
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
                ArrayList<ArrayList<String>> columnFunctions = new ArrayList<>();
                ArrayList<String> refFunctionList = new ArrayList<>();
                StringTokenizer st = new StringTokenizer(dataConstraint, ";");
                
                String numberFunction = st.nextToken();
                String refFunction = st.nextToken();
                while (st.hasMoreTokens()) {
                    ArrayList<String> columnFunction = new ArrayList<>();
                    StringTokenizer stt = new StringTokenizer(st.nextToken(), ",");
                    while (stt.hasMoreTokens()) {
                        columnFunction.add(stt.nextToken());
                    }
                    columnFunctions.add(columnFunction);
                }
                
                st = new StringTokenizer(refFunction, "$");
                while (st.hasMoreTokens()) {
                    refFunctionList.add(st.nextToken());
                }
                
                //Fill the list of primarykeys
                String primaryKeyColumns = "*";
                i = 0;
                for (String primaryKey : rel.getPrimaryKey()) {
                    if (i == 0) {
                        primaryKeyColumns = primaryKey;
                    } else {
                        primaryKeyColumns += ", " + primaryKey;
                    }
                    i++;
                }
                String selectStatement = "SELECT " + primaryKeyColumns + " FROM " + rel.getTableName();
                this.primaryKeyValues = this.dbConn.executeSQLSelectStatement(
                        this.conf.getString("auth.user"),
                        this.conf.getString("auth.pass"),
                        selectStatement
                );
                
                this.generateDataFromFunction(rel.getTableName(), refFunctionList, columnFunctions, numberFunction);
            }
            
            //Reset the primarykey and reference lists
            this.primaryKeyValues = new ArrayList<>();
            this.primaryKeyAssignments = new ArrayList<>();
            this.referenceValues = new ArrayList<>();
            this.referenceAssignments = new ArrayList<>();
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
        this.columns = st.countTokens() - 2;
    }
    
    private void generateDataFromFunction(String tableName, ArrayList<String> refFunctionList, ArrayList<ArrayList<String>> columnFunctions, String numberFunction ) throws MySQLAlchemistException {
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
        
        ArrayList<ArrayList<ArrayList<String>>> functions = new ArrayList<>();
        for (ArrayList<String> columnFunction : columnFunctions) {
            if (columnFunction.get(0).equals("ref")) {
                ArrayList<String> referenceAssigment = new ArrayList<>();
                referenceAssigment.add("" + i);
                referenceAssigment.add(columnFunction.get(1));
                referenceAssigment.add(columnFunction.get(2));
                this.referenceAssignments.add(referenceAssigment);

                String selectStatement = "SELECT " + columnFunction.get(2) + " FROM " + columnFunction.get(1);
                ArrayList<ArrayList<String>> referenceColumn = this.dbConn.executeSQLSelectStatement(
                        this.conf.getString("auth.user"),
                        this.conf.getString("auth.pass"),
                        selectStatement
                );
                String columnName = referenceColumn.get(0).get(0);
                referenceColumn.get(0).set(0, columnFunction.get(1) + "." + columnName);
                this.referenceValues.add(referenceColumn);
            }
        }
        
        switch (refFunctionList.get(0)) {
            /*
             * NOT USEBALL FOR NOW
             *
             *
            case "refAll": {
                String refColumnName = refFunctionList.get(2);
                ArrayList<ArrayList<String>> referenceList = new ArrayList<>();
                i = 0;
                for (ArrayList<ArrayList<String>> referenceValue : this.referenceValues) {
                    if (referenceValue.get(0).get(0).equals(refColumnName)) {
                        referenceList = this.referenceValues.get(i);
                    }
                    i++;
                }
                
                for (i = 1; i < referenceList.size(); i++) {
                    String[] dataRow = new String[this.columns];
                    
                    int j = 0;
                    for (ArrayList<String> columnFunction : columnFunctions) {
                        //Handle normal columns that are not primaryKey and not part of refAll
                        if (!this.primaryKeyValues.get(0).contains(columnFunction.get(0).toUpperCase())) {
                            ArrayList<String> params = new ArrayList<>();
                            int k = 0;
                            for (String param : columnFunction) {
                                if (k != 0) {
                                    params.add(param);
                                }
                                k++;
                            }
                            
                            if (!columnFunction.get(0).equals("ref")) {
                                dataRow[j] = this.findAndExecuteFunction(columnFunction.get(0), params);
                            } else {
                                for (ArrayList<ArrayList<String>> referenceValue : this.referenceValues) {
                                    if (referenceValue.get(0).get(0).equals(columnFunction.get(2))) {
                                        Random rd = new Random();
                                        int randomInt = rd.nextInt(referenceValue.size());
                                        dataRow[j] = referenceValue.get(randomInt).get(0);
                                    }
                                }
                            }
                        }
                        
                        //Handle refAll function
                        if (!this.primaryKeyValues.get(0).contains(referenceList.get(0).get(0).toUpperCase())) {
                            dataRow[j] = referenceList.get(i).get(0);
                        }
                        
                        j++;
                    }
                    
                    //Handle primary keys
                    boolean primaryKeyExists = true;
                    ArrayList<String> primaryKey = new ArrayList<>();
                    j = 0;
                    while (primaryKeyExists) {
                        int k = 0;
                        for (int columnIndex : primaryKeyColumnIndex) {
                            if (this.primaryKeyValues.get(0).get(k).equals(referenceList.get(0).get(0).toUpperCase())) {
                                primaryKey.add(referenceList.get(i).get(0));
                            } else {
                                ArrayList<String> params = new ArrayList<>();
                                int l = 0;
                                for (String param : columnFunctions.get(columnIndex)) {
                                    if (l != 0) {
                                        params.add(param);
                                    }
                                    l++;
                                }
                                if (!columnFunctions.get(columnIndex).get(0).equals("ref")) {
                                    primaryKey.add(this.findAndExecuteFunction(columnFunctions.get(columnIndex).get(0), params));
                                } else {
                                    for (ArrayList<ArrayList<String>> referenceValue : this.referenceValues) {
                                        if (referenceValue.get(0).get(0).equals(columnFunctions.get(columnIndex).get(2).toUpperCase())) {
                                            Random rd = new Random();
                                            int randomInt = rd.nextInt(referenceValue.size());
                                            primaryKey.add(referenceValue.get(randomInt).get(0));
                                        }
                                    }
                                }
                            }
                            k++;
                        }
                        if (!this.primaryKeyValues.contains(primaryKey)) {
                            primaryKeyExists = false;
                        }
                        if (j > 200) {
                            throw new MySQLAlchemistException("Kein freier Primary-Key gefunden.", new Exception());
                        }
                        j++;
                    }
                    
                    j = 0;
                    for (int columnIndex : primaryKeyColumnIndex) {
                        dataRow[columnIndex] = primaryKey.get(j);
                        j++;
                    }
                    this.primaryKeyValues.add(primaryKey);
                    
                    this.generateAndExecuteInsertStatement(tableName, dataRow);
                }
            }
            
            case "refRandom": {
                
            }
             */
            
            default: {                
                for (i = 1; i < number; i++) {
                    String[] dataRow = new String[this.columns];
                    
                    int j = 0;
                    for (ArrayList<String> columnFunction : columnFunctions) {
                        //Handle normal columns that are not primaryKey and not part of refAll
                        if (!this.primaryKeyValues.get(0).contains(columnFunction.get(0).toUpperCase())) {
                            ArrayList<String> params = new ArrayList<>();
                            int k = 0;
                            for (String param : columnFunction) {
                                if (k != 0) {
                                    params.add(param);
                                }
                                k++;
                            }
                            if (!columnFunction.get(0).equals("ref")) {
                                dataRow[j] = this.findAndExecuteFunction(columnFunction.get(0), params);
                            } else {
                                for (ArrayList<ArrayList<String>> referenceValue : this.referenceValues) {
                                    if (referenceValue.get(0).get(0).equals(columnFunction.get(1) + "." + columnFunction.get(2).toUpperCase())) {
                                        Random rd = new Random();
                                        int randomInt = rd.nextInt(referenceValue.size());
                                        dataRow[j] = referenceValue.get(randomInt).get(0);
                                    }
                                }
                            }
                        }
                        j++;
                    }
                    
                    //Handle primary keys
                    boolean primaryKeyExists = true;
                    ArrayList<String> primaryKey = new ArrayList<>();
                    j = 0;
                    while (primaryKeyExists) {
                        primaryKey = new ArrayList<>();
                        for (int columnIndex : primaryKeyColumnIndex) {
                            ArrayList<String> params = new ArrayList<>();
                            int k = 0;
                            for (String param : columnFunctions.get(columnIndex)) {
                                if (k != 0) {
                                    params.add(param);
                                }
                                k++;
                            }
                            if (!columnFunctions.get(columnIndex).get(0).equals("ref")) {
                                primaryKey.add(this.findAndExecuteFunction(columnFunctions.get(columnIndex).get(0), params));
                            } else {
                                for (ArrayList<ArrayList<String>> referenceValue : this.referenceValues) {
                                    if (referenceValue.get(0).get(0).equals(columnFunctions.get(columnIndex).get(1) + "." + columnFunctions.get(columnIndex).get(2).toUpperCase())) {
                                        Random rd = new Random();
                                        int randomInt = rd.nextInt(referenceValue.size() - 1) + 1;
                                        primaryKey.add(referenceValue.get(randomInt).get(0));
                                    }
                                }
                            }
                        }
                        if (!this.primaryKeyValues.contains(primaryKey)) {
                            primaryKeyExists = false;
                        }
                        if (j > 200) {
                            throw new MySQLAlchemistException("Kein freier Primary-Key gefunden.", new Exception());
                        }
                        j++;
                    }
                    j = 0;
                    for (int columnIndex : primaryKeyColumnIndex) {
                        dataRow[columnIndex] = primaryKey.get(j);
                        j++;
                    }
                    this.primaryKeyValues.add(primaryKey);
                    
                    this.generateAndExecuteInsertStatement(tableName, dataRow);
                }
            }
        }
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
                    case("int+"):{
                        result = generateIntegerPos();
                        break;
                    }
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
                    case("business"):{
                        result = generateBusinessName();
                        break;
                    }
                    case("street"):{
                        result = generateStreetName();
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
            
            case("between"):{
                String para1;
                int para2;
                int para3;
                if(params.size() == 3){
                para1 = params.get(0);
                para2 = Integer.parseInt(params.get(1));
                para3 = Integer.parseInt(params.get(2));
                } else {
                    throw new MySQLAlchemistException("3 Parameter werden bei between benötigt", new Exception());
                }
                switch(para1){
                    case("int"):{
                        result = generateBetweenInteger(para2, para3);
                        break;
                    }
                    case("double"):{
                        result = generateBetweenDouble(para2, para3);
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
    
    private void generateAndExecuteInsertStatement(String tableName, String[] dataRow) throws MySQLAlchemistException {
        String insertedValues = "";
        for (int i = 0; i < dataRow.length; i++) {
            if (i == 0) {
                insertedValues += dataRow[i];
            } else {
                insertedValues += ", " + dataRow[i];
            }
        }
        this.dbConn.executeSQLUpdateStatement(
                this.conf.getString("auth.user"),
                this.conf.getString("auth.pass"),
                "INSERT INTO " + tableName + " VALUES(" + insertedValues + ")"
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
    
    public String generateIntegerPos() {
        Random r = new Random();
        int i = r.nextInt();
        if(i < 0){
            i = i*(-1);
        }
        String result = "" + i;
        return result;
    }
    
    public String generateDouble() {
        Random r = new Random();
        String result = "" + r.nextDouble();
        return result;
    }
    
    public String generateString(int length) {
        DataFactory df = new DataFactory();
        String result = "'" + df.getRandomChars(length) + "'";
        return result;
    }
    
    public String generateDate() {
        DataFactory df = new DataFactory();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(df.getBirthDate());
        String result = "'" + date + "'";
        return result;
    }
    
    public String generateBusinessName() {
        DataFactory df = new DataFactory();
        String result = "'" + df.getBusinessName() + "'";
        return result;
    }
    
    public String generateStreetName() {
        DataFactory df = new DataFactory();
        String result = "'" + df.getStreetName() + "'";
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
    
    public String generateBetweenInteger(int min, int max) {
        DataFactory df = new DataFactory();
        String result = "" + df.getNumberBetween(min, max);
        return result;
    }
    
    public String generateBetweenDouble(int min, int max) {
        DataFactory df = new DataFactory();
        String result = "" + df.getNumberBetween(min, max-1) + "." + df.getNumberBetween(0, 99);
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
            String path = this.conf.getString("input.dataGenPath");
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
