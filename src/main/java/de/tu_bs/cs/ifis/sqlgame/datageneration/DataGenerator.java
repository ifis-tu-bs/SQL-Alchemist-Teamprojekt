package de.tu_bs.cs.ifis.sqlgame.datageneration;

import com.typesafe.config.*;
import net.sf.jsqlparser.*;

import de.tu_bs.cs.ifis.sqlgame.dbconnection.DBConnection;
import de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.Exercise;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.Relation;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserTokenManager;

/**
 * Class DataGenerator.
 * 
 * Class to generate custom insert statements for the tabes of a task.
 * 
 * @author Tobias Gruenhagen, Philip Holzhueter, Tobias Runge
 */
public class DataGenerator {
    
    /**
     * List with the relations of the task
     */
    private final ArrayList<Relation> relations;
    
    /**
     * List with the exercises of the task
     */
    private final ArrayList<Exercise> exercises;
    
    /**
     * Database connection to speak with the database.
     */
    private final DBConnection dbConn;
    
    /**
     * Number of columns of the actual relation.
     */
    private int columns = 0;
    
    /**
     * ArrayList of the primary keys assigned to the column index.
     * 
     * Form: [[first primary key name, column index],
     *        [second primary key name, column index], ...]
     */
    private ArrayList<ArrayList<String>> primaryKeyAssignments = new ArrayList<>();
    
    /**
     * ArrayList of the primary values.
     * 
     * Form: [[first primary key name, second primary key name, ...],
     *        [first primary key value 1, second primary key value 1, ...],
     *        [first primary key value 2, second primary key value 2, ...], ...]
     */
    private ArrayList<ArrayList<String>> primaryKeyValues = new ArrayList<>();
    
    /**
     * ArrayList of the reference values.
     * 
     * Form: [[[first reference name], [first reference value 1],
     *         [first reference value 1], ...]
     *        [[second reference name], [second reference value 1]
     *         [second reference value 1], ...], ...]
     */
    private ArrayList<ArrayList<ArrayList<String>>> referenceValues = new ArrayList<>();
    
    /**
     * Config to load dynamic paths.
     */
    private final Config conf = ConfigFactory.load();
    
    /**
     * Constructor DataGenerator.
     * 
     * Initialize the local attributes relations and dbConn with the given
     * parameter.
     * 
     * @param relations ArrayList<Relation> list with the relations of the task
     * @param exercises ArrayList<Exercise> list with the exercises of the task
     * @param dbConn DBConnection database connection to execute sql statements
     */
    public DataGenerator(ArrayList<Relation> relations, ArrayList<Exercise> exercises, DBConnection dbConn) {
        this.relations = relations;
        this.exercises = exercises;
        this.dbConn = dbConn;
    }
    
    /**
     * Method generateFixExtension.
     * 
     * Grab the reference select statements and generate an extension so that
     * there is always enough data if the select statement is executed.
     * 
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     */
    public void generateSelectExtension() throws MySQLAlchemistException {
        //Iterate through all exercises of the task
        for (Exercise exe : this.exercises) {
            String selectStatement = exe.getReferencestatement();
            selectStatement = selectStatement.toLowerCase();
            ArrayList<String> tablesToSelectFrom = null;
            ArrayList<ArrayList<String>> columnsNeedData = null;
            
            CCJSqlParserManager spm = new CCJSqlParserManager();
            //HIER WEITER
            
            StringTokenizer st = new StringTokenizer(selectStatement, "from");
            //String in front of first from is not needed
            st.nextToken();
            String withTablestoSelectFrom = st.nextToken();
            
            st = new StringTokenizer(selectStatement);
            
            if (st.nextToken().equals("select")) {
                
            } else {
                throw new MySQLAlchemistException("Das Referenzstatement ist kein SELECT - Statement.", new Exception());
            }
        }
    }
    
    /**
     * Method generateFixExtension.
     * 
     * Grab the extension to be generated and generate the extension. Generate
     * insert statements for each table of the task based on the tuples
     * of the xml file.
     * 
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     */
    public void generateFixExtension() throws MySQLAlchemistException {
        //Iterate through all relations of the task
        for (Relation rel : this.relations) {
            //Break if no data has to be generated
            if (rel.getDataGeneration().isEmpty()) {
                break;
            }
            //Calculate the column number and the primary key assignments of
            //the actual relation
            this.calculateColumns(rel);
            this.calculatePrimaryKeyAssignments(rel);
            
            //Iterate through all data constraints
            for (String dataConstraint : rel.getDataGeneration()) {
                StringTokenizer st = new StringTokenizer(dataConstraint, ";");
                
                //First token for the number function
                String numberFunction = st.nextToken();
                
                //Second token for the reference function
                String refFunction = st.nextToken();
                ArrayList<String> refFunctionList = new ArrayList<>();
                StringTokenizer stt = new StringTokenizer(refFunction, ",");
                while (stt.hasMoreTokens()) {
                    refFunctionList.add(stt.nextToken());
                }
                
                //The following tokens for the column function of the relation
                ArrayList<ArrayList<String>> columnFunctions = new ArrayList<>();
                while (st.hasMoreTokens()) {
                    ArrayList<String> columnFunction = new ArrayList<>();
                    stt = new StringTokenizer(st.nextToken(), ",");
                    while (stt.hasMoreTokens()) {
                        columnFunction.add(stt.nextToken());
                    }
                    columnFunctions.add(columnFunction);
                }

                //Fill primary key values list
                //Build the sql select statement to get the primary key columns
                String primaryKeyColumns = "*";
                int i = 0;
                for (String primaryKey : rel.getPrimaryKey()) {
                    if (i == 0) {
                        primaryKeyColumns = primaryKey;
                    } else {
                        primaryKeyColumns += ", " + primaryKey;
                    }
                    i++;
                }
                //Execute the sql select statement to get the primary key columns
                this.primaryKeyValues = this.dbConn.executeSQLSelectStatement(
                        this.conf.getString("auth.user"),
                        this.conf.getString("auth.pass"),
                        "SELECT " + primaryKeyColumns + " FROM " + rel.getTableName()
                );
                
                //Generate and execute the insert statements with the grabed data
                int number = generateNumber(numberFunction);
                this.generateDataFromFunction(rel.getTableName(), number, refFunctionList, columnFunctions);
            }
            
            //Reset the primarykey and reference lists for the new data constraint
            this.primaryKeyValues = new ArrayList<>();
            this.primaryKeyAssignments = new ArrayList<>();
            this.referenceValues = new ArrayList<>();
        }
    }
    
    /**
     * Method calculatePrimaryKeyAssignments.
     * 
     * Get the primary keys of the given relation and assigne its column index
     * to it.
     * 
     * @param relation Relation
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     */
    private void calculatePrimaryKeyAssignments(Relation relation) throws MySQLAlchemistException {
        //Get the column names from the given relation/table
        String selectStatement = "SELECT * FROM " + relation.getTableName();
        ArrayList<String> columnNames = this.dbConn.executeSQLSelectStatement(
                this.conf.getString("auth.user"),
                this.conf.getString("auth.pass"),
                selectStatement
        ).get(0);
        
        //Iterate through the list of the column names and build a new
        //primary key assignment for every primary key column
        ArrayList<String> primaryKeyAssignment;
        int i = 0;
        for (String columnName : columnNames) {
            //New primary key assignment if the actual column is a primary key
            if (relation.getPrimaryKey().contains(columnName.toLowerCase())) {
                primaryKeyAssignment = new ArrayList<>();
                primaryKeyAssignment.add(columnName);
                primaryKeyAssignment.add("" + i);
                this.primaryKeyAssignments.add(primaryKeyAssignment);
            }
            i++;
        }
    }
    
    /**
     * Method calculate Columns.
     * 
     * Method to calculate the columns of the given relation/table.
     * 
     * @param relation Relation
     */
    private void calculateColumns(Relation relation) {
        //Count the tokens of the given metadata row (this is the column number
        //minus the token for the number and the token for the reference type
        String tuple = relation.getDataGeneration().get(0);
        StringTokenizer st = new StringTokenizer(tuple, ";");
        this.columns = st.countTokens() - 2;
    }
    
    /**
     * Method generateNumber.
     * 
     * Generate a number from the given number function. This can be just an
     * integer value of for example a random integer between two values.
     * 
     * @param numberFunction
     * @return
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     */
    private int generateNumber(String numberFunction) throws MySQLAlchemistException {
        StringTokenizer st = new StringTokenizer(numberFunction, ",");
        //Get the function name of the number function
        String functionName = st.nextToken();
        //Get the parameter of the number function
        ArrayList<String> params = new ArrayList<>();
        //If the number function has parameter add them to the list
        //if not, return the function name which is the number
        if (st.hasMoreTokens()) {
            params.add(st.nextToken());
        } else {
            return Integer.parseInt(functionName);
        }
        
        //Calculate the number by the specific function and parameter
        int number = 1;
        switch(functionName) {
            //Calculate a number in a specific span of two given integer
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
        
        return number;
    }
    
    /**
     * Method generateDataFromFunction.
     * 
     * Generate and execute the insert statements with the given data.
     * 
     * @param tableName String name of the given table
     * @param numberFunction number of the inserted statements
     * @param refFunctionList ArrayList<String> list of the reference function
     * @param columnFunctions ArrayList<ArrayList<String>> list of the column functions
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     */
    private void generateDataFromFunction(String tableName, int number, ArrayList<String> refFunctionList, ArrayList<ArrayList<String>> columnFunctions) throws MySQLAlchemistException {
        //Iterate through the columnFunctions to save the referencing columns
        int i = 0;
        for (ArrayList<String> columnFunction : columnFunctions) {
            //If the actual column is a referencing column, get the referenced values
            //and save them in the local list
            if (columnFunction.get(0).equals("ref")) {
                ArrayList<ArrayList<String>> referenceColumn = this.dbConn.executeSQLSelectStatement(
                        this.conf.getString("auth.user"),
                        this.conf.getString("auth.pass"),
                        "SELECT " + columnFunction.get(2) + " FROM " + columnFunction.get(1)
                );
                String columnName = referenceColumn.get(0).get(0);
                //Replace the column name of the referenced value by the unique pair of table name and column name
                referenceColumn.get(0).set(0, columnFunction.get(1) + "." + columnName.toLowerCase());
                this.referenceValues.add(referenceColumn);
            }
            i++;
        }
        
        //Switch the given reference type
        switch (refFunctionList.get(0)) {
            //No reference type
            case "none": {
                //Generate insert statements
                this.generateInsertStatements("none", tableName, number, columnFunctions, null, 0);
                
                break;
            }
            
            case "refAll": {
                String refTableName = refFunctionList.get(1);
                String refColumnName = refFunctionList.get(2);
                
                ArrayList<ArrayList<String>> referenceList = new ArrayList<>();
                i = 0;
                for (ArrayList<ArrayList<String>> referenceValue : this.referenceValues) {
                    if (referenceValue.get(0).get(0).equals(refTableName + "." + refColumnName)) {
                        referenceList = this.referenceValues.get(i);
                    }
                    i++;
                }
                
                //Iterate through every referenced value
                for (i = 1; i < referenceList.size(); i++) {
                    //Generate insert statements
                    this.generateInsertStatements("refAll", tableName, number, columnFunctions, referenceList, i);
                }
                
                break;
            }
            
            case "refRandom": {
                String refTableName = refFunctionList.get(1);
                String refColumnName = refFunctionList.get(2);
                
                ArrayList<ArrayList<String>> referenceList = new ArrayList<>();
                i = 0;
                for (ArrayList<ArrayList<String>> referenceValue : this.referenceValues) {
                    if (referenceValue.get(0).get(0).equals(refTableName + "." + refColumnName)) {
                        referenceList = this.referenceValues.get(i);
                    }
                    i++;
                }
                
                //Generate insert statements
                this.generateInsertStatements("refRandom", tableName, number, columnFunctions, referenceList, 0);
                
                break;
            }
        }
    }
    
    /**
     * Method generate InsertStatements.
     * 
     * Generate the insert statements with the given data.
     * 
     * @param generateType String type of generation strategy: none, refAll, refRandom
     * @param tableName String name of the given table
     * @param number int number of the insert statements to be generated
     * @param columnFunctions ArrayList<ArrayList<String>> list of the column functions
     * @param referenceList ArrayList<ArrayList<String>> list of the referenced values
     * @param referenceIndex int index of the reference value if generateType is refAll
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     */
    private void generateInsertStatements(
            String generateType,
            String tableName,
            int number,
            ArrayList<ArrayList<String>> columnFunctions,
            ArrayList<ArrayList<String>> referenceList,
            int referenceIndex
    ) throws MySQLAlchemistException {
        //Create and execute "number" times insert statements 
        for (int i = 1; i <= number; i++) {
            //New Stringarray for the inserted data
            String[] dataRow = new String[this.columns];

            //Iterate through the column functions to generate a value for every column
            int j = 0;
            for (ArrayList<String> columnFunction : columnFunctions) {
                //Handle the normal columns that are not a primary key
                if (!this.primaryKeyValues.get(0).contains(columnFunction.get(0))) {
                    //Get the parameter for the column function
                    ArrayList<String> params = new ArrayList<>();
                    int k = 0;
                    for (String param : columnFunction) {
                        if (k != 0) {
                            params.add(param);
                        }
                        k++;
                    }

                    //Get a value for the actual column
                    if (!columnFunction.get(0).equals("ref")) {
                        //Generate a new value if it is no referencing column
                        dataRow[j] = this.findAndExecuteFunction(columnFunction.get(0), params);
                    } else {
                        if (generateType.equals("refAll") || generateType.equals("refRandom")) {
                            //Proof if the referencing column is the reference function column
                            if (referenceList.get(0).get(0).equals(columnFunction.get(1) + "." + columnFunction.get(2))) {
                                if (generateType.equals("refAll")) {
                                    //Get one value by order of the reference list
                                    dataRow[j] = referenceList.get(referenceIndex).get(0);
                                }
                                if (generateType.equals("refRandom")) {
                                    //Get one value by random of the reference list
                                    Random rd = new Random();
                                    int randomInt = rd.nextInt(referenceList.size() - 1) + 1;
                                    dataRow[j] = referenceList.get(randomInt).get(0);
                                }
                            } else {
                                //Get a value of the reference list if it is a referencing column
                                //Iterate through reference value list
                                for (ArrayList<ArrayList<String>> referenceValue : this.referenceValues) {
                                    //Get a random value if the actual referenceValue belongs to the actual column
                                    if (referenceValue.get(0).get(0).equals(columnFunction.get(1) + "." + columnFunction.get(2))) {
                                        Random rd = new Random();
                                        int randomInt = rd.nextInt(referenceValue.size() - 1) + 1;
                                        dataRow[j] = referenceValue.get(randomInt).get(0);
                                    }
                                }
                            }
                        } else {
                            //Get a value of the reference list if it is a referencing column
                            //Iterate through reference value list
                            for (ArrayList<ArrayList<String>> referenceValue : this.referenceValues) {
                                //Get a random value if the actual referenceValue belongs to the actual column
                                if (referenceValue.get(0).get(0).equals(columnFunction.get(1) + "." + columnFunction.get(2))) {
                                    Random rd = new Random();
                                    int randomInt = rd.nextInt(referenceValue.size() - 1) + 1;
                                    dataRow[j] = referenceValue.get(randomInt).get(0);
                                }
                            }
                        }
                    }
                }
                j++;
            }

            //Handle the primary keys and search for one until it is a unique one
            boolean primaryKeyExists = true;
            ArrayList<String> primaryKey = new ArrayList<>();
            j = 0;
            while (primaryKeyExists) {
                //Reset the primary key
                primaryKey = new ArrayList<>();
                //Iterate through all primary keys
                for (ArrayList<String> primaryKeyAssignment : this.primaryKeyAssignments) {
                    int columnIndex = Integer.parseInt(primaryKeyAssignment.get(1));

                    //Get the parameter for the column function
                    ArrayList<String> params = new ArrayList<>();
                    int k = 0;
                    for (String param : columnFunctions.get(columnIndex)) {
                        if (k != 0) {
                            params.add(param);
                        }
                        k++;
                    }

                    //Get a value for the actual column
                    if (!columnFunctions.get(columnIndex).get(0).equals("ref")) {
                        //Generate a new value if it is no referencing column
                        primaryKey.add(this.findAndExecuteFunction(columnFunctions.get(columnIndex).get(0), params));
                    } else {
                        if (generateType.equals("refAll") || generateType.equals("refRandom")) {
                            //Proof if the referencing column is the reference function column
                            if (referenceList.get(0).get(0).equals(columnFunctions.get(columnIndex).get(1) + "." + columnFunctions.get(columnIndex).get(2))) {
                                if (generateType.equals("refAll")) {
                                    //Get one value by order of the reference list
                                    primaryKey.add(referenceList.get(referenceIndex).get(0));
                                }
                                if (generateType.equals("refRandom")) {
                                    //Get one value by random of the reference list
                                    Random rd = new Random();
                                    int randomInt = rd.nextInt(referenceList.size() - 1) + 1;
                                    primaryKey.add(referenceList.get(randomInt).get(0));
                                }
                            } else {
                                //Get a value of the reference list if it is a referencing column
                                //Iterate through reference value list
                                for (ArrayList<ArrayList<String>> referenceValue : this.referenceValues) {
                                    //Get a random value if the actual referenceValue belongs to the actual column
                                    if (referenceValue.get(0).get(0).equals(columnFunctions.get(columnIndex).get(1) + "." + columnFunctions.get(columnIndex).get(2))) {
                                        Random rd = new Random();
                                        int randomInt = rd.nextInt(referenceValue.size() - 1) + 1;
                                        primaryKey.add(referenceValue.get(randomInt).get(0));
                                    }
                                }
                            }
                        } else {
                            //Get a value of the reference list if it is a referencing column
                            //Iterate through reference value list
                            for (ArrayList<ArrayList<String>> referenceValue : this.referenceValues) {
                                //Get a random value if the actual referenceValue belongs to the actual column
                                if (referenceValue.get(0).get(0).equals(columnFunctions.get(columnIndex).get(1) + "." + columnFunctions.get(columnIndex).get(2))) {
                                    Random rd = new Random();
                                    int randomInt = rd.nextInt(referenceValue.size() - 1) + 1;
                                    primaryKey.add(referenceValue.get(randomInt).get(0));
                                }
                            }
                        }
                    }
                }

                //Leave the while scope if the new primary key does not exist
                if (!this.primaryKeyValues.contains(primaryKey)) {
                    primaryKeyExists = false;
                }

                //Throw an exception if no primary key is found after 200 iterations
                if (j > 200) {
                    throw new MySQLAlchemistException("Kein freier Primary-Key gefunden.", new Exception());
                }

                j++;
            }

            //Insert the new primary key in the data row
            j = 0;
            for (ArrayList<String> primaryKeyAssignment : this.primaryKeyAssignments) {
                int columnIndex = Integer.parseInt(primaryKeyAssignment.get(1));
                dataRow[columnIndex] = primaryKey.get(j);
                j++;
            }
            //Add the new primary to the primary key values of the actual relation/table
            this.primaryKeyValues.add(primaryKey);

            //Execute an insert statement based on the data row
            this.executeInsertStatements(tableName, dataRow);
        }
    }
    
    /**
     * Method generateAndExecuteInsertStatement.
     * 
     * Generate and execute an insert statement with the given data row into
     * the given table.
     * 
     * @param tableName String name of the table in which the data is inserted
     * @param dataRow String[] data which is inserted
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     */
    private void executeInsertStatements(String tableName, String[] dataRow) throws MySQLAlchemistException {
        //Build the insert statement as a string from the data row stringarray
        String insertedValues = "";
        for (int i = 0; i < dataRow.length; i++) {
            //Only insert no comma if it is the first value
            if (i == 0) {
                insertedValues += dataRow[i];
            } else {
                insertedValues += ", " + dataRow[i];
            }
        }
        //Excute the insert statement
        this.dbConn.executeSQLUpdateStatement(
                this.conf.getString("auth.user"),
                this.conf.getString("auth.pass"),
                "INSERT INTO " + tableName + " VALUES(" + insertedValues + ")"
        );
    }
    
    /**
     * Method findAndExecuteFunction.
     * 
     * This method finds the specific function to generate data from the
     * given function name and other parameters.
     * 
     * @param functionName the name of the fuction
     * @param params a list of paramters for the function
     * @return string with the generated data
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     *         Exception, if the user give not the correct parameters
     */
    private String findAndExecuteFunction(String functionName, ArrayList<String> params) throws MySQLAlchemistException{
        String result = "";
        GenerateSpecificData gd = new GenerateSpecificData();
        switch (functionName) {
            case("random"): {
                String para = params.get(0);
                switch (para) {
                    case("int+"):{
                        result = gd.generateIntegerPos();
                        break;
                    }
                    case("int"):{
                        result = gd.generateInteger();
                        break;
                    }
                    case("double"):{
                        result = gd.generateDouble();
                        break;
                    }
                    case("string"):{
                        int para2 = 5;
                        if(params.size() == 2){
                        para2 = Integer.parseInt(params.get(1));
                        }
                        result = gd.generateString(para2);
                        break;
                    }
                    case("word"):{
                        int para2 = 5;
                        if(params.size() == 2){
                        para2 = Integer.parseInt(params.get(1));
                        }
                        result = gd.generateWord(para2);
                        break;
                    }
                    case("text"):{
                        int para2 = 20;
                        if(params.size() == 2){
                        para2 = Integer.parseInt(params.get(1));
                        }
                        result = gd.generateText(para2);
                        break;
                    }
                    case("firstname"):{
                        result = gd.generateFirstName();
                        break;
                    }
                    case("lastname"):{
                        result = gd.generateLastName();
                        break;
                    }
                    case("fullname"):{
                        result = gd.generateFullName();
                        break;
                    }
                    case("date"):{
                        result = gd.generateDate();
                        break;
                    }
                    case("business"):{
                        result = gd.generateBusinessName();
                        break;
                    }
                    case("street"):{
                        result = gd.generateStreetName();
                        break;
                    }
                    case("city"):{
                        result = gd.generateCity();
                        break;
                    }
                    case("adress"):{
                        result = gd.generateAdress();
                        break;
                    }
                    case("email"):{
                        result = gd.generateEmail();
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
                        result = gd.generateCustomData(para, random, def);
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
                        result = gd.generateMinInteger(para2);
                        break;
                    }
                    case("double"):{
                        result = gd.generateMinDouble(para2);
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
                        result = gd.generateMaxInteger(para2);
                        break;
                    }
                    case("double"):{
                        result = gd.generateMaxDouble(para2);
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
                        result = gd.generateBetweenInteger(para2, para3);
                        break;
                    }
                    case("double"):{
                        result = gd.generateBetweenDouble(para2, para3);
                        break;
                    }
                }
                break;
            }
            
            case("gauss"):{
                String para1;
                double para2;
                double para3;
                if(params.size() == 3){
                    para1 = params.get(0);
                    para2 = Double.parseDouble(params.get(1));
                    para3 = Double.parseDouble(params.get(2));
                } else {
                    throw new MySQLAlchemistException("3 Parameter werden bei gauss benötigt", new Exception());
                }
                switch(para1){
                    case("int"):{
                        result = gd.generateGaussInt(para2, para3);
                        break;
                    }
                    case("double"):{
                        result = gd.generateGaussDouble(para2, para3);
                        break;
                    }
                }
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
                result = gd.generateList(para);
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
}
