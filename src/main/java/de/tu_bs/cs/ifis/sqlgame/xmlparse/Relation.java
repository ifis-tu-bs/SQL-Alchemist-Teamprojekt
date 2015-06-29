package de.tu_bs.cs.ifis.sqlgame.xmlparse;

import java.util.ArrayList;

/**
 * class for the tables and inserts of the tasks
 *
 * @author Tobias Gruenhagen, Philip Holzhueter, Tobias Runge
 */
public class Relation {

    private String intension;
    private String tableName;
    private ArrayList<ArrayList> columnInformation;
    private ArrayList<String> tuple;
    private ArrayList<String> generationTuples;
    private ArrayList<String> primaryKey;

    /**
     * constructor without parameters
     */
    public Relation() {
        this.tuple = new ArrayList<>();
        this.generationTuples = new ArrayList<>();
        this.primaryKey = new ArrayList<>();

    }

    /**
     * get-method for the create-table-statement
     *
     * @return intension
     */
    public String getIntension() {
        return intension;
    }

    /**
     * set-method for the create-table-statement
     *
     * @param intension String, the create-table-statement
     */
    public void setIntension(String intension) {
        this.intension = intension;
    }
    
    /**
     * get-method for the name of the table
     *
     * @return tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * set-method for the column information.
     *
     * @param name String column information
     */
    public void setTableName(String name) {
        this.tableName = name;
    }
    
    /**
     * get-method for the column information.
     *
     * @return ArrayList column information
     */
    public ArrayList<ArrayList> getColumnInformation() {
        return columnInformation;
    }

    /**
     * set-method for the column information.
     *
     * @param columnInformation ArrayList list with the column information
     */
    public void setColumnInformation(ArrayList<ArrayList> columnInformation) {
        this.columnInformation = columnInformation;
    }

    /**
     * get-method for the insert-into-statements as a list
     *
     * @return tuple as an array
     */
    public ArrayList<String> getTuple() {
        return this.tuple;
    }

    /**
     * get-method for the insert-statements as a string
     *
     * @return tuple as a string
     */
    public String getTupleAsString() {
        String s = "";
        
        for (String item : tuple) {
            s += item + "\n";
        }
        return s;
    }

    /**
     * set-method for the insert-into-statements
     *
     * @param tuple the insert-into-statements
     */
    public void setTuple(String tuple) {
        this.tuple.add(tuple);
    }
    
    /**
     * get-method for the contraint tuples
     *
     * @return generationTuples tuples as an array
     */
    public ArrayList<String> getDataGeneration() {
        return this.generationTuples;
    }

    /**
     * set-method for the generationTuples contraints
     *
     * @param data the generationTuples tuple
     */
    public void setDataGeneration(String data) {
        this.generationTuples.add(data);
    }
    
        /**
     * get-method for the primary Keys
     *
     * @return primary Key as an array
     */
    public ArrayList<String> getPrimaryKey() {
        return this.primaryKey;
    }

    /**
     * set-method for the generationTuples contraints
     *
     * @param key the primary key
     */
    public void setPrimaryKey(String key) {
        this.primaryKey.add(key);
    }

    /**
     * method to make a string of the relations
     *
     * @return string with all content
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getIntension() + "\n" + getTupleAsString());
        return sb.toString();
    }
}
