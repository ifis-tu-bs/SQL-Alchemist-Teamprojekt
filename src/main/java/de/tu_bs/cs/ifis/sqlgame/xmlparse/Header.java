package de.tu_bs.cs.ifis.sqlgame.xmlparse;

/**
 * class for the title and other texts in the task
 *
 * @author Tobias
 */
public class Header {

    //private String tasks;
    private String taskId;
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
     */

    /**
     * get-method for the taskid
     *
     * @return taskid
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * set-method for the taskid
     *
     * @param taskId String, the id of the task
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * get-method for the title of the exercise
     *
     * @return title
     */
    public String[] getTitle() {
        return title;
    }

    /**
     * set-method for the title of the exercise
     *
     * @param title String, title of the task
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
     * get-method for the introductiontext of the exercise
     *
     * @return flufftext
     */
    public String[] getFlufftext() {
        return flufftext;
    }

    /**
     * set-method for the introductiontext of the exercise
     *
     * @param flufftext the introductiontext of the exercise
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
     * get-method for the langugage of the exercise
     *
     * @return language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * set-method for the language of the exercise
     *
     * @param language String, the language of the task
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * method to make a string out of the header of the exercise sheet
     *
     * @return string with all content
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
