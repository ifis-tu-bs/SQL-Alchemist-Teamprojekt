package sandbox;

import dbconnection.*;
import org.h2.tools.DeleteDbFiles;
import xmlparse.MySAXParser;
import xmlparse.XMLSyntaxCheck;

/**
 * Class Task.
 * 
 * Manage a task (create task, close task, update DB, load from DB,
 * check existence)
 * 
 * @author Tobias
 */
public class Task {
    
    private String name = "";
    private String dbName = "";
    private int players = 0;
    
    private MySAXParser mySaxParser;
    private DBConnection tmpDbConn;
    private final DBConnection fixDbConn = new DBConnection("jdbc:h2:./dbs/sql-alchemist-teamprojekt");
    
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
     * Getter for mysaxp.
     * 
     * @return mySaxParser
     */
    public MySAXParser getMySaxParser() {
        return mySaxParser;
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
     * Setter for mysaxp.
     * 
     * @param mySaxParser MySaxParser
     */
    public void setMySaxParser(MySAXParser mySaxParser) {
        this.mySaxParser = mySaxParser;
    }
    
    /**
     * Constructor Task.
     * 
     * @param name String, name of the task
     * @param dbName String, name of the db
     */
    public Task(String name, String dbName) {
        this.name = name;
        this.dbName = dbName;
    }
    
    /**
     * Method loadTask.
     * 
     * Load a task and its properties from db depending on the taskname.
     * Save the properties in the local attributes.
     */
    public void loadTask() {
        String selectStatement = "SELECT * FROM Task WHERE name = ?";
        String[] variables = new String[1];
        variables[0] = this.name;
        
        String[][] result = fixDbConn.executeSQLSelectPreparedStatement("", "", selectStatement, variables);
        this.name = result[0][1];
        this.dbName = result[1][1];
        this.players = Integer.parseInt(result[2][1]);
    }
    
    /**
     * Method updateTask.
     * 
     * Update the properties of the given task in the db with the values of
     * the local attributes.
     */
    public void updateTask() {
        String[] variables = new String[3];
        variables[0] = this.dbName;
        variables[1] = "" + this.players;
        variables[2] = this.name;
        String updateStatement = "UPDATE Task SET db_name = ?, players = ? WHERE name = ?";

        fixDbConn.executeSQLUpdatePreparedStatement("", "", updateStatement, variables);
    }
    
    /**
     * Method startTask.
     * 
     * Start a new task with a given filename. It is now checked whether the
     * task already exists or not. If there is a task, the taskoptions are
     * loaded. If not, a new task and a new db is created.
     * 
     * @return Task, loaded or created task
     */
    public Task startTask() {
        if (this.checkTask()) {
            this.loadTask();
            
            //Set db for task
            String dbUrl = "jdbc:h2:./dbs/" + this.dbName;
            this.tmpDbConn = new DBConnection(dbUrl);
            this.setTmpDbConn(this.tmpDbConn);
            
            //Make the xml-sructure-check
            XMLSyntaxCheck sych = new XMLSyntaxCheck();
            sych.checkxml(this.name + ".xml");

            //Parse the xml-file und build the db-tables
            MySAXParser msp = new MySAXParser(tmpDbConn);
            msp.parseDocument(this.name + ".xml");
            this.setMySaxParser(msp);
            
            //Update #players
            int playerNum = this.getPlayers() + 1;
            this.setPlayers(playerNum);
            
            this.updateTask();
        } else {
            this.players = 1;
            this.createTask();
        }
        
        return this;
    }
    
    /**
     * Method createTask.
     * 
     * Insert a new task-entry into the db and build up a new dbconnection.
     */
    private void createTask() {
        String insertStatement = "INSERT INTO Task VALUES('" + this.name + "', '" + this.dbName + "', " + this.players + ")";
        
        this.fixDbConn.executeSQLUpdateStatement("", "", insertStatement);
        
        //Set db for task
        String dbUrl = "jdbc:h2:./dbs/" + this.dbName;
        this.tmpDbConn = new DBConnection(dbUrl);
        this.setTmpDbConn(this.tmpDbConn);
        
        //Make the xml-sructure-check
        XMLSyntaxCheck sych = new XMLSyntaxCheck();
        sych.checkxml(this.name + ".xml");
        
        //Parse the xml-file und build the db-tables
        MySAXParser msp = new MySAXParser(this.tmpDbConn);
        msp.parseAndCreateDb(this.name + ".xml");
        this.mySaxParser = msp;
    }
    
    /**
     * Method closeTask.
     * 
     * Close a task and increment the number of players playing the given task.
     * If it was the last player that played the task, the taskentry in the db
     * is deleted.
     */
    public void closeTask() {
        int playerNum = this.players - 1;
        this.players = playerNum;
        this.updateTask();
        
        //Delete taskentry in DB if #players = 0
        if (this.players == 0) {
            String deleteStatement = "DELETE FROM Task WHERE name = ?";
            String[] variables = new String[1];
            variables[0] = this.name;

            //Delete the database for the task
            DeleteDbFiles.execute("./dbs", this.dbName, true);

            fixDbConn.executeSQLUpdatePreparedStatement("", "", deleteStatement, variables);
        }
    }
    
    /**
     * Method checkTask.
     * 
     * Check whether the given task already exists in the db or not.
     * 
     * @return boolean, true if the given task exists 
     */
    public boolean checkTask() {
        String selectStatement = "SELECT * FROM Task WHERE name = ?";
        String[] variables = new String[1];
        variables[0] = this.name;
        
        String[][] result = fixDbConn.executeSQLSelectPreparedStatement("", "", selectStatement, variables);
        return result[0][0] != null;
    }
}
