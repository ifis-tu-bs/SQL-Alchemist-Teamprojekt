package sandbox;

import dbconnection.DBConnection;
import xmlparse.MySAXParser;
import xmlparse.XMLSyntaxCheck;

/**
 * Class Game.
 * 
 * Handle and manage the overall gameoptions. E.g.: Start a new game or end
 * an existing game.
 * 
 * @author Tobias
 */
public class Game {
    
    /**
     * Constructor Game.
     */
    public Game() {
        //Nothing to do.
    }
    
    /**
     * Method startGame.
     * 
     * Start a new game with a given task. It is now checked whether the task
     * already exists or not. If the is a task, the taskoptions are loaded. If
     * not, a new task and a new db is created.
     * 
     * @param taskName String, name of the task the player wants to play
     * @return Task, loaded or created task
     */
    public Task startGame(String taskName) {
        Task newTask = new Task(taskName, taskName, false);
        
        if (newTask.checkTask(taskName)) {
            newTask.loadTask(taskName);
            DBConnection tmpDbConn = new DBConnection("jdbc:h2:./dbs/" + taskName);
            newTask.setTmpDbConn(tmpDbConn);
            
            //Make the xml-sructure-check
            XMLSyntaxCheck sych = new XMLSyntaxCheck();
            sych.checkxml(taskName + ".xml");

            //Parse the xml-file und build the db-tables
            MySAXParser msp = new MySAXParser(tmpDbConn);
            msp.parseAndCreateDb(taskName + ".xml");
            newTask.setMySaxParser(msp);
            
            //Update #players
            int players = newTask.getPlayers() + 1;
            newTask.setPlayers(players);
            
            newTask.updateTask(taskName);
        } else {
            newTask.setName(taskName);
            newTask.setDbName(taskName);
            newTask.setDbMem(false);
            newTask.setPlayers(1);
            newTask.createTask();
        }
        
        return newTask;
    }
    
    /**
     * Method endGame.
     * 
     * End a game an increment the number of players playing the given task.
     * If it was the last player that played the task, the taskentry in the db
     * is deleted.
     * 
     * @param task Task, actual task that the player played
     */
    public void endGame(Task task) {
        int players = task.getPlayers() - 1;
        task.setPlayers(players);
        task.updateTask(task.getName());
        
        //Delete taskentry in DB if #players = 0
        if (task.getPlayers() == 0) {
            task.closeTask();
        }
    }
}
