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

    public String[] getTerm() {
        return term;
    }

    public void setTerm(String[] term) {
        this.term = term;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
