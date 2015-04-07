/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlparse;

/**
 *
 * @author Tobias
 */
public class Task {

    private String tasktexts;
    private String referencestatement;
    private String evaluationstrategy;
    private String[] term;
    private int points;

    public Task() {
        this.term = new String[0];
    }

    public String getTasktexts() {
        return tasktexts;
    }

    public void setTasktexts(String tasktexts) {
        this.tasktexts = tasktexts;
    }

    public String getReferencestatement() {
        return referencestatement;
    }

    public void setReferencestatement(String referencestatement) {
        this.referencestatement = referencestatement;
    }

    public String getEvaluationstrategy() {
        return evaluationstrategy;
    }

    public void setEvaluationstrategy(String evaluationstrategy) {
        this.evaluationstrategy = evaluationstrategy;
    }

    public String getTerm() {
        String s = "";
        for (int i = 0; i < this.term.length; i++) {

            s += this.term[i] + "\n";
        }
        return s;
    }

    public void setTerm(String term) {
        String[] temp = new String[this.term.length + 1];
        for (int i = 0; i < this.term.length; i++) {
            temp[i] = this.term[i];
        }
        temp[temp.length - 1] = term;
        this.term = temp;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Task \n");
        sb.append("Tasktext:" + this.getTasktexts() + "\n");
        sb.append("Referencestatement:" + this.getReferencestatement() + "\n");
        sb.append("Evaluationstrategy:" + this.getEvaluationstrategy() + "\n");
        sb.append("Requiredterms:" + this.getTerm() + "\n");
        sb.append("Points:" + this.getPoints() + "\n");

        return sb.toString();
    }
}
