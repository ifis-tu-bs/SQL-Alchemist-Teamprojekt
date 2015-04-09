package xmlparse;

/**
 * class for the title and other texts in the task
 *
 * @author Tobias
 */
public class Header {

    //private String tasks;
    //private String task;
    private String[] title;
    private String[] flufftext;
    private String language;

    /**
     * constructor
     */
    public Header() {
        this.title = new String[0];
        this.flufftext = new String[0];
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
    public String[] getTitle() {
        return title;
    }

    /**
     * set-method for the title
     *
     * @param title
     */
    public void setTitle(String title) {
        String[] temp = new String[this.title.length + 1];
        for (int i = 0; i < this.title.length; i++) {
            temp[i] = this.title[i];
        }
        temp[temp.length - 1] = title;
        this.title = temp;
    }

    /**
     * get-method for the text
     *
     * @return flufftext
     */
    public String[] getFlufftext() {
        return flufftext;
    }

    /**
     * set-method for the text
     *
     * @param flufftext
     */
    public void setFlufftext(String flufftext) {
        String[] temp = new String[this.flufftext.length + 1];
        for (int i = 0; i < this.flufftext.length; i++) {
            temp[i] = this.flufftext[i];
        }
        temp[temp.length - 1] = flufftext;
        this.flufftext = temp;
    }

    /**
     * get-method for the langugage
     *
     * @return language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * set-method for the language
     *
     * @param language
     */
    public void setLanguage(String language) {
        this.language = language;
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
        for (int i = 0; i < this.getTitle().length; i++) {

            sb.append("Title:" + this.title[i] + "\n");
        }
        for (int i = 0; i < this.getFlufftext().length; i++) {
            sb.append("text:" + this.flufftext[i] + "\n");
        }
        return sb.toString();
    }
}
