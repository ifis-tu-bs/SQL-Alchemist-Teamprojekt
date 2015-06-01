package de.tu_bs.cs.ifis.sqlgame.xmlparse;

import java.util.ArrayList;

/**
 * class for the tables and inserts of the tasks
 *
 * @author Tobias
 */
public class Relation {

    private String intension;
    private String tableName;
    private ArrayList<String> tuple;
    private ArrayList<String> dataGeneration;
    private ArrayList<String> primaryKey;

    /**
     * constructor without parameters
     */
    public Relation() {
        this.tuple = new ArrayList<>();
        this.dataGeneration = new ArrayList<>();
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
     * set-method for the name of the table
     *
     * @param name String, the name of the table
     */
    public void setTableName(String name) {
        this.tableName = name;
    }

    /**
     * get-method for the insert-into-statements as a list
     *
     * @return tuple as an array
     */
    public ArrayList getTuple() {
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
     * @return dataGeneration tuples as an array
     */
    public ArrayList getDataGeneration() {
        return this.dataGeneration;
    }

    /**
     * set-method for the dataGeneration contraints
     *
     * @param data the dataGeneration tuple
     */
    public void setDataGeneration(String data) {
        this.dataGeneration.add(data);
    }
    
        /**
     * get-method for the primary Keys
     *
     * @return primary Key as an array
     */
    public ArrayList getPrimaryKey() {
        return this.primaryKey;
    }

    /**
     * set-method for the dataGeneration contraints
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
