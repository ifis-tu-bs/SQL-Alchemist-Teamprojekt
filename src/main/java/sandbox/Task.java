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

    public String getName() {
        return name;
    }

    public String getDbName() {
        return dbName;
    }

    public boolean isDbMem() {
        return dbMem;
    }
    
    public int getPlayers() {
        return players;
    }

    public DBConnection getTmpDbConn() {
        return tmpDbConn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setDbMem(boolean dbMem) {
        this.dbMem = dbMem;
    }
    
    public void setPlayers(int players) {
        this.players = players;
    }

    public void setTmpDbConn(DBConnection tmpDbConn) {
        this.tmpDbConn = tmpDbConn;
    }
    
    public Task(String name, String dbName, boolean dbMem) {
        this.name = name;
        this.dbName = dbName;
        this.dbMem = dbMem;
    }
    
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
    
    public void updateTask(String name) {
        String[] variables = new String[4];
        variables[0] = this.dbName;
        variables[1] = "" + this.dbMem;
        variables[2] = "" + this.players;
        variables[3] = this.name;
        String updateStatement = "UPDATE Task SET db_name = ?, db_mem = ?, players = ? WHERE name = ?";

        fixDbConn.executeSQLUpdatePreparedStatement("", "", updateStatement, variables);
    }
    
    public void createTask() {
        String insertStatement = "INSERT INTO Task VALUES('" + this.name + "', '" + this.dbName + "', " + this.dbMem + ", " + this.players + ")";
        
        fixDbConn.executeSQLUpdateStatement("", "", insertStatement);
        
        tmpDbConn = new DBConnection("./dbs", this.dbName);
        this.setTmpDbConn(tmpDbConn);
    }
    
    public void closeTask() {
        String deleteStatement = "DELETE FROM Task WHERE name = ?";
        String[] variables = new String[1];
        variables[0] = this.name;
        
        //Delete the database for the task
        DeleteDbFiles.execute("./dbs", this.dbName, true);
        
        fixDbConn.executeSQLUpdatePreparedStatement("", "", deleteStatement, variables);
    }
    
    public boolean checkTask(String taskName) {
        String selectStatement = "SELECT * FROM Task WHERE name = ?";
        String[] variables = new String[1];
        variables[0] = taskName;
        
        String[][] result = fixDbConn.executeSQLSelectPreparedStatement("", "", selectStatement, variables);
        return result[0][0] != null;
    }
}
