/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlparse;

/**
 *
 * @author Tobias
 */
public class Header {

    private String tasks;
    private String task;
    private String title;
    private String fluffytext;

    public Header() {
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFluffytext() {
        return fluffytext;
    }

    public void setFluffytext(String fluffytext) {
        this.fluffytext = fluffytext;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Header \n");
        sb.append("Title:" + this.getTitle() + "\n");
        sb.append("text:" + this.getFluffytext() + "\n");

        return sb.toString();
    }
}
