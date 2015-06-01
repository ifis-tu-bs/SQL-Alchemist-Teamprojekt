package de.tu_bs.cs.ifis.sqlgame.xmlparse;

import java.util.ArrayList;

/**
 * class for the subtasks in the exercise
 *
 * @author Tobias
 */
public class Exercise {

    private ArrayList<String> tasktexts;
    private String referencestatement;
    private String evaluationstrategy;
    private ArrayList<String> term;
    private int points;
    private String language;

    /**
     * constructor without parameters
     */
    public Exercise() {
        this.tasktexts = new ArrayList<>();
        this.term = new ArrayList<>();
    }

    /**
     * get-method for the tasktext. The defintion of the problem, 
     * that has to be solved
     *
     * @return tasktext
     */
    public ArrayList<String> getTasktexts() {
        return tasktexts;
    }

    /**
     * set-method for the tasktext. The defintion of the problem, 
     * that has to be solved
     *
     * @param tasktexts String, the text of the task
     */
    public void setTasktexts(String tasktexts) {
        this.tasktexts.add(tasktexts);
    }

    /**
     * get-method for the referencestatement (the solution of the task)
     *
     * @return referencestatement
     */
    public String getReferencestatement() {
        return referencestatement;
    }

    /**
     * set-method for the referencestatement (the solution of the task)
     *
     * @param referencestatement String, statement for the solution
     */
    public void setReferencestatement(String referencestatement) {
        this.referencestatement = referencestatement;
    }

    /**
     * get-method for the evaluationstrategy (Set or List)
     *
     * @return evaluationstrategy
     */
    public String getEvaluationstrategy() {
        return evaluationstrategy;
    }

    /**
     * set-method for the evaluationstrategy (Set or List)
     *
     * @param evaluationstrategy String with Set or List
     */
    public void setEvaluationstrategy(String evaluationstrategy) {
        this.evaluationstrategy = evaluationstrategy;
    }

    /**
     * get-method for the requied terms, which the user need for this task
     *
     * @return all terms as a string
     */
    public String getTerm() {
        String s = "";
        for (String term : this.term) {
            s += term + "\n";
        }
        return s;
    }

    /**
     * set-method for the requied terms, which the user need for this task
     *
     * @param term String, the term that should be added to the array
     */
    public void setTerm(String term) {
        this.term.add(term);
    }

    /**
     * get-method for the points of the task
     *
     * @return points
     */
    public int getPoints() {
        return points;
    }

    /**
     * set-method for the points of the task
     *
     * @param points int, the points of the task
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * get-method for the langugage of the task
     *
     * @return language of the task
     */
    public String getLanguage() {
        return language;
    }

    /**
     * set-method for the language of the task
     *
     * @param language String, language of the tast
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * method to make a string out of the tasks
     *
     * @return String with all content
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Task \n");
        for (String taskText : this.tasktexts) {
            sb.append("Tasktext:" + taskText + "\n");
        }
        sb.append("Referencestatement:" + this.getReferencestatement() + "\n");
        sb.append("Evaluationstrategy:" + this.getEvaluationstrategy() + "\n");
        sb.append("Requiredterms:" + this.getTerm() + "\n");
        sb.append("Points:" + this.getPoints() + "\n");

        return sb.toString();
    }
}
