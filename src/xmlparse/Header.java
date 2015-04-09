package xmlparse;

/**
 * class for the title and other texts in the task
 *
 * @author Tobias
 */
public class Header {

    //private String tasks;
    //private String task;
    private String title;
    private String flufftext;

    /**
     * constructor
     */
    public Header() {
    }
    /*
     public String getTasks() {
     return tasks;
     }

     public void setTasks(String tasks) {
     this.tasks = tasks;
     }

     public String getTask() {
     return task;
     }

     public void setTask(String task) {
     this.task = task;
     }
     */

    /**
     * get-method for the title
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * set-method for the title
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * get-method for the text
     *
     * @return flufftext
     */
    public String getFlufftext() {
        return flufftext;
    }

    /**
     * set-method for the text
     *
     * @param flufftext
     */
    public void setFlufftext(String flufftext) {
        this.flufftext = flufftext;
    }

    /**
     * method to make a string
     *
     * @return string
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Header \n");
        sb.append("Title:" + this.getTitle() + "\n");
        sb.append("text:" + this.getFlufftext() + "\n");

        return sb.toString();
    }
}
