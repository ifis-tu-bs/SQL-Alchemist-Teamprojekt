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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Philips
 */
public class DataGenerator {
    
    private List<Relation> relations;
    private DBConnection dbConn;
    
    private final Config conf = ConfigFactory.load();
    
    
    public DataGenerator(List<Relation> relations, DBConnection dbConn) {
        this.relations = relations;
        this.dbConn = dbConn;
    }
    
    public void generateData() throws MySQLAlchemistException {
        for (Relation rel : this.relations) {
            int columns = this.calculateColumns(rel);
            
            ArrayList<ArrayList<String>> dataList = new ArrayList<>();
            for (int i = 0; i < columns; i++) {
                ArrayList<String> newArrayList = new ArrayList<>();
                dataList.add(newArrayList);
            }
            
            for (String dataConstraint : rel.getDataGeneration()) {
                ArrayList<String> columnFunctions = new ArrayList<>();
                StringTokenizer st = new StringTokenizer(dataConstraint, ";");
                
                String numberRefFunction = st.nextToken();
                while (st.hasMoreTokens()) {
                    columnFunctions.add(st.nextToken());
                }
                
                StringTokenizer stt = new StringTokenizer(numberRefFunction, "&");
                String numberFunction = stt.nextToken();
                String refType = "none";
                ArrayList<ArrayList<String>> primaryKeys = new ArrayList<>();
                if (stt.hasMoreTokens()) {
                    refType = stt.nextToken();
                    String selectStatement = "SELECT * FROM " + rel.getTableName();
                    ArrayList<ArrayList<String>> refTable = this.dbConn.executeSQLSelectStatement(
                            this.conf.getString("auth.user"),
                            this.conf.getString("auth.pass"),
                            selectStatement
                    );
                    int i = 0;
                    for (String columnName : refTable.get(0)) {
                        if (rel.getPrimaryKey().contains(columnName.toLowerCase())) {
                            ArrayList<String> primaryKeyValues = new ArrayList<>();
                            boolean hasMoreElements = true;
                            int elementIndex = i;
                            while (hasMoreElements) {
                                primaryKeyValues.add(refTable.get(1).get(elementIndex));
                                elementIndex = elementIndex + columns;
                                if (refTable.get(1).get(elementIndex).isEmpty()) {
                                    hasMoreElements = false;
                                }
                            }
                            primaryKeys.add(primaryKeyValues);
                        }
                        
                        i++;
                    }
                }
                
                switch (refType) {
                    case "none": {
                        dataList = this.generateDataFromFunction(columnFunctions, dataList, numberFunction);
                        break;
                    }
                    
                    case "refAll": {
                        
                        break;
                    }
                    
                    case "refOne": {
                        
                    }
                    
                    case "refRandom": {
                        
                    }
                }
                
                this.generateAndExecuteInsertStatements(dataList);
            }
        }
    }
        
    private int calculateColumns(Relation rel) {
        String tuple = rel.getDataGeneration().get(0);
        StringTokenizer st = new StringTokenizer(tuple, ";");
        return st.countTokens() - 1;
    }
    
    private ArrayList<ArrayList<String>> generateDataFromFunction(ArrayList<String> columnFunctions, ArrayList<ArrayList<String>> dataList, String numberFunction) {
        for (String columnFunction : columnFunctions) {
            StringTokenizer st = new StringTokenizer(columnFunction, "&");
            String functionName = st.nextToken();

            StringTokenizer stt = new StringTokenizer(columnFunction, ",");
            ArrayList<String> params = new ArrayList<>();
            while (stt.hasMoreTokens()) {
                params.add(stt.nextToken());                        
            }

            int number = this.generateNumber(numberFunction);
            for (int i = 0; i < number; i++) {
                dataList.get(i).add(this.findAndExecuteFunction(functionName, params));
            }
        }
        
        return dataList;
    }
    
    private int generateNumber(String numberFunction) {
        return 0;
    }
    
    private ArrayList<String> findAndExecuteFunction(int quantity, String functionName, ArrayList<String> params) throws MySQLAlchemistException{
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
        }
        
        return result;
    }
    
    private void generateAndExecuteInsertStatements(ArrayList<ArrayList<String>> dataList) {
        
    }
    
    public ArrayList<String> generateFirstName(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add(df.getFirstName());
        }
        return result;
    }
        
    public ArrayList<String> generateLastName(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add(df.getLastName());
        }
        return result;
    }
    
    public ArrayList<String> generateFullName(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add(df.getFirstName() + " " + df.getLastName());
        }
        return result;
    }
    
    public ArrayList<String> generateInteger(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add("" + df.getNumber());
        }
        return result;
    }
    
    public ArrayList<String> generateDouble(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add(df.getNumber() + "" + df.getNumber());
        }
        return result;
    }
    
    public ArrayList<String> generateDate(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            //TODO: result.add(df.getBirthDate());
        }
        return result;
    }
    
    public ArrayList<String> generateCity(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add(df.getCity());
        }
        return result;
    }
    
    public ArrayList<String> generateAdress(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add(df.getAddress());
        }
        return result;
    }
    
    public ArrayList<String> generateEmail(int quantity) {
        DataFactory df = new DataFactory();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < quantity; i++){
            result.add(df.getEmailAddress());
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
                result.add(df.getItem(valuesStringArray, random, defaultValue));
            }
            return result;
        } catch (IOException e) {
            throw new MySQLAlchemistException("Fehler beim Generieren", e);
        }
        
    }
}
