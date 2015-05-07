package de.tu_bs.cs.ifis.sqlgame.xmlparse;

/**
 * class for the tables and inserts of the tasks
 *
 * @author Tobias
 */
public class Relation {

    private String intension;
    private String[] tuple;

    /**
     * constructor without parameters
     */
    public Relation() {
        this.tuple = new String[0];

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
     * get-method for the insert-into-statements as a list
     *
     * @return tuple as an array
     */
    public String[] getTuple() {
        return this.tuple;
    }

    /**
     * get-method for the insert-statements as a string
     *
     * @return tuple as a string
     */
    public String getTupleAsString() {
        String s = "";
        for (int i = 0; i < this.tuple.length; i++) {

            s += this.tuple[i] + "\n";
        }
        return s;
    }

    /**
     * set-method for the insert-into-statements
     *
     * @param tuple the insert-into-statements
     */
    public void setTuple(String tuple) {
        String[] temp = new String[this.tuple.length + 1];
        for (int i = 0; i < this.tuple.length; i++) {
            temp[i] = this.tuple[i];
        }
        temp[temp.length - 1] = tuple;
        this.tuple = temp;
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
