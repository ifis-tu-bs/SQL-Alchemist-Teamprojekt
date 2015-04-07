/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlparse;

/**
 *
 * @author Tobias
 */
public class Relation {

    private String intension;
    private String[] tuple;

    public Relation() {
        this.tuple = new String[0];

    }

    public Relation(String intension, String[] tuple) {
        this.intension = intension;
        this.tuple = tuple;
    }

    public String getIntension() {
        return intension;
    }

    public void setIntension(String intension) {
        this.intension = intension;
    }

    public String getTuple() {
        String s = "";
        for (int i = 0; i < this.tuple.length; i++) {

            s += this.tuple[i] + "\n";
        }
        return s;
    }

    public void setTuple(String tuple) {
        String[] temp = new String[this.tuple.length + 1];
        for (int i = 0; i < this.tuple.length; i++) {
            temp[i] = this.tuple[i];
        }
        temp[temp.length - 1] = tuple;
        this.tuple = temp;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Relation \n");
        sb.append("Intension:" + getIntension() + "\n");
        sb.append("Tuple:" + getTuple() + "\n");

        return sb.toString();
    }
}
