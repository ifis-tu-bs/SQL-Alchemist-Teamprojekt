package de.tu_bs.cs.ifis.sqlgame.xmlparse;

import java.util.ArrayList;

/**
 * class for the title and other texts in the task
 *
 * @author Tobias
 */
public class Header {

    //private String tasks;
    private String taskId;
    private ArrayList<String> title;
    private ArrayList<String> flufftext;
    private String language;

    /**
     * constructor
     */
    public Header() {
        this.title = new ArrayList<>();
        this.flufftext = new ArrayList<>();
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
    public ArrayList<String> getTitle() {
        return title;
    }

    /**
     * set-method for the title of the exercise
     *
     * @param title String, title of the task
     */
    public void setTitle(String title) {
        this.title.add(title);
    }

    /**
     * get-method for the introductiontext of the exercise
     *
     * @return flufftext
     */
    public ArrayList<String> getFlufftext() {
        return flufftext;
    }

    /**
     * set-method for the introductiontext of the exercise
     *
     * @param flufftext the introductiontext of the exercise
     */
    public void setFlufftext(String flufftext) {
        this.flufftext.add(flufftext);
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
        for (String title : this.title) {

            sb.append("Title:" + title + "\n");
        }
        for (String flufftext : this.flufftext) {
            sb.append("text:" + flufftext + "\n");
        }
        return sb.toString();
    }
}
