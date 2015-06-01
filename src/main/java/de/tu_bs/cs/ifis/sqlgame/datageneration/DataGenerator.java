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
    
    private String findAndExecuteFunction(String functionName, ArrayList<String> params) {
        switch (functionName) {
            
        }
        
        return "";
    }
    
    private void generateAndExecuteInsertStatements(ArrayList<ArrayList<String>> dataList) {
        
    }
    
    public void generateFullname() {
        DataFactory df = new DataFactory();
        for (int i = 0; i < 100; i++) {
            String name = df.getFirstName() + " "+ df.getLastName();
            System.out.println(name);
        }
    }
    
    public void generateStringInt(String metaData, int quantity, String defaultValue) throws MySQLAlchemistException {
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
            for (int i = 0; i < quantity; i++) {
                System.out.println(df.getItem(valuesStringArray,95,defaultValue));
            }
        } catch (IOException e) {
            throw new MySQLAlchemistException("Fehler beim Generieren", e);
        }
        
    }
}
