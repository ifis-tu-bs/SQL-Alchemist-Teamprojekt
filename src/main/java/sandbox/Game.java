/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sandbox;

/**
 *
 * @author Tobias
 */
public class Game {
    String taskName = "";
    String dbName = "";
    
    public Game(String taskName) {
        this.taskName = taskName;
         
        Task newTask = new Task(taskName, taskName, false);
        
        if (newTask.checkTask(taskName)) {
            System.out.println("hier");
            newTask.loadTask(taskName);
            this.dbName = newTask.getDbName();
            int players = newTask.getPlayers() + 1;
            System.out.println(players);
            newTask.setPlayers(players);
            newTask.updateTask(taskName);
        } else {
            newTask.setName(taskName);
            newTask.setDbName(taskName);
            newTask.setDbMem(false);
            newTask.setPlayers(1);
            newTask.createTask();
        }
    }
    
    public void endGame(Task task) {
        int players = task.getPlayers() - 1;
        task.setPlayers(players);
        task.updateTask(task.getName());
        
        task.closeTask();
    }
}
