package sandbox;

import dbconnection.*;
import org.h2.tools.DeleteDbFiles;

/**
 *
 * @author Tobias
 */
public class Task {
    private String name = "";
    private String dbName = "";
    private boolean dbMem = false;
    private int players = 0;
    
    private DBConnection tmpDbConn;
    private final DBConnection fixDbConn = new DBConnection("./dbs", "sql-alchemist-teamprojekt");
    
    /**
     * Getter for name.
     * 
     * @return String
     */
    public String getName() {
        return name;
    }
    
    /**
     * Getter for dbName.
     * 
     * @return String
     */
    public String getDbName() {
        return dbName;
    }
    
    /**
     * Getter for dbMem.
     * 
     * @return boolean
     */
    public boolean isDbMem() {
        return dbMem;
    }
    
    /**
     * Getter for players.
     * 
     * @return int
     */
    public int getPlayers() {
        return players;
    }
    
    /**
     * Getter for tmpDbConn.
     * 
     * @return DBConnection
     */
    public DBConnection getTmpDbConn() {
        return tmpDbConn;
    }
    
    /**
     * Setter for name.
     * 
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Setter for dbName.
     * 
     * @param dbName String
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    
    /**
     * Setter for dbMem.
     * 
     * @param dbMem boolean
     */
    public void setDbMem(boolean dbMem) {
        this.dbMem = dbMem;
    }
    
    /**
     * Setter for players.
     * 
     * @param players int
     */
    public void setPlayers(int players) {
        this.players = players;
    }
    
    /**
     * Setter for tmpDbConn.
     * 
     * @param tmpDbConn DBConnection
     */
    public void setTmpDbConn(DBConnection tmpDbConn) {
        this.tmpDbConn = tmpDbConn;
    }
    
    /**
     * Constructor Task.
     * 
     * @param name String, name of the task
     * @param dbName String, name of the db
     * @param dbMem boolean, true if it is a memorydb
     */
    public Task(String name, String dbName, boolean dbMem) {
        this.name = name;
        this.dbName = dbName;
        this.dbMem = dbMem;
    }
    
    /**
     * Method loadTask.
     * 
     * Load a task and its properties from db depending on the given taskname.
     * Save the properties in the local attributes.
     * 
     * @param name String, name of the task 
     */
    public void loadTask(String name) {
        String selectStatement = "SELECT * FROM Task WHERE name = ?";
        String[] variables = new String[1];
        variables[0] = name;
        
        String[][] result = fixDbConn.executeSQLSelectPreparedStatement("", "", selectStatement, variables);
        this.name = result[0][1];
        this.dbName = result[1][1];
        this.dbMem = Boolean.parseBoolean(result[2][1]);
        this.players = Integer.parseInt(result[3][1]);
    }
    
    /**
     * Method updateTask.
     * 
     * Update the properties of the given task in the db with the values of
     * the local attributes.
     * 
     * @param name String, name of the task
     */
    public void updateTask(String name) {
        String[] variables = new String[4];
        variables[0] = this.dbName;
        variables[1] = "" + this.dbMem;
        variables[2] = "" + this.players;
        variables[3] = this.name;
        String updateStatement = "UPDATE Task SET db_name = ?, db_mem = ?, players = ? WHERE name = ?";

        fixDbConn.executeSQLUpdatePreparedStatement("", "", updateStatement, variables);
    }
    
    /**
     * Method createTask.
     * 
     * Insert a new task-entry into the db and build up a new dbconnection.
     */
    public void createTask() {
        String insertStatement = "INSERT INTO Task VALUES('" + this.name + "', '" + this.dbName + "', " + this.dbMem + ", " + this.players + ")";
        
        fixDbConn.executeSQLUpdateStatement("", "", insertStatement);
        
        tmpDbConn = new DBConnection("./dbs", this.dbName);
        this.setTmpDbConn(tmpDbConn);
    }
    
    /**
     * Method closeTask.
     * 
     * Delete the actual task from the db and delete the associated task-db.
     */
    public void closeTask() {
        String deleteStatement = "DELETE FROM Task WHERE name = ?";
        String[] variables = new String[1];
        variables[0] = this.name;
        
        //Delete the database for the task
        DeleteDbFiles.execute("./dbs", this.dbName, true);
        
        fixDbConn.executeSQLUpdatePreparedStatement("", "", deleteStatement, variables);
    }
    
    /**
     * Method checkTask.
     * 
     * Check whether the given task already exists in the db or not.
     * 
     * @param taskName String, name of the task
     * @return boolean, true if the given task exists 
     */
    public boolean checkTask(String taskName) {
        String selectStatement = "SELECT * FROM Task WHERE name = ?";
        String[] variables = new String[1];
        variables[0] = taskName;
        
        String[][] result = fixDbConn.executeSQLSelectPreparedStatement("", "", selectStatement, variables);
        return result[0][0] != null;
    }
}
