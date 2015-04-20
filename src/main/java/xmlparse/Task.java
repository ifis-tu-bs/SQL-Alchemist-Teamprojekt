package xmlparse;

/**
 * class for the subtasks in the exercise
 *
 * @author Tobias
 */
public class Task {

    private String[] tasktexts;
    private String referencestatement;
    private String evaluationstrategy;
    private String[] term;
    private int points;
    private String language;

    /**
     * constructor without parameters
     */
    public Task() {
        this.tasktexts = new String[0];
        this.term = new String[0];
    }

    /**
     * get-method for the tasktext. The defintion of the problem, 
     * that has to be solved
     *
     * @return tasktext
     */
    public String[] getTasktexts() {
        return tasktexts;
    }

    /**
     * set-method for the tasktext. The defintion of the problem, 
     * that has to be solved
     *
     * @param tasktexts
     */
    public void setTasktexts(String tasktexts) {
        String[] temp = new String[this.tasktexts.length + 1];
        for (int i = 0; i < this.tasktexts.length; i++) {
            temp[i] = this.tasktexts[i];
        }
        temp[temp.length - 1] = tasktexts;
        this.tasktexts = temp;
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
     * @param referencestatement
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
     * @param evaluationstrategy
     */
    public void setEvaluationstrategy(String evaluationstrategy) {
        this.evaluationstrategy = evaluationstrategy;
    }

    /**
     * get-method for the requied terms, which the user need for this task
     *
     * @return terms
     */
    public String getTerm() {
        String s = "";
        for (int i = 0; i < this.term.length; i++) {

            s += this.term[i] + "\n";
        }
        return s;
    }

    /**
     * set-method for the requied terms, which the user need for this task
     *
     * @param term
     */
    public void setTerm(String term) {
        String[] temp = new String[this.term.length + 1];
        for (int i = 0; i < this.term.length; i++) {
            temp[i] = this.term[i];
        }
        temp[temp.length - 1] = term;
        this.term = temp;
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
     * @param points
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * get-method for the langugage of the task
     *
     * @return language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * set-method for the language of the task
     *
     * @param language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * method to make a string out of the tasks
     *
     * @return string
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Task \n");
        for (int i = 0; i < this.getTasktexts().length; i++) {
            sb.append("Tasktext:" + this.tasktexts[i] + "\n");
        }
        sb.append("Referencestatement:" + this.getReferencestatement() + "\n");
        sb.append("Evaluationstrategy:" + this.getEvaluationstrategy() + "\n");
        sb.append("Requiredterms:" + this.getTerm() + "\n");
        sb.append("Points:" + this.getPoints() + "\n");

        return sb.toString();
    }
}
