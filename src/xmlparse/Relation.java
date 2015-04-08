package xmlparse;

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
     * constructor with parameters
     *
     * @param intension
     * @param tuple
     */
    public Relation(String intension, String[] tuple) {
        this.intension = intension;
        this.tuple = tuple;
    }

    /**
     * get-method for the table
     *
     * @return intension
     */
    public String getIntension() {
        return intension;
    }

    /**
     * set-metohd for the table
     *
     * @param intension
     */
    public void setIntension(String intension) {
        this.intension = intension;
    }

    /**
     * get-method for the insert-into-statements
     *
     * @return
     */
    public String getTuple() {
        String s = "";
        for (int i = 0; i < this.tuple.length; i++) {

            s += this.tuple[i] + "\n";
        }
        return s;
    }

    /**
     * set-method for the insert-into-statements
     *
     * @param tuple
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
     * method to make a string
     *
     * @return string
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getIntension() + getTuple());
        return sb.toString();
    }
}
