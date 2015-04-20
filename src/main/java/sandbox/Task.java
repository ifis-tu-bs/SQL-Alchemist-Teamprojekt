/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sandbox;

import dbconnection.*;

/**
 *
 * @author Tobias
 */
public class Task {
    private String name = "";
    private String dbName = "";
    private boolean dbMem = false;
    private int players = 0;
    
    private DBConnection dbConn = new DBConnection("org.h2.Driver", "./dbs", "sql-alchemist-teamprojekt");

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
    
    public Task(String name, String dbName, boolean dbMem) {
        this.name = name;
        this.dbName = dbName;
        this.dbMem = dbMem;
    }
    
    public void loadTask(String name) {
        String selectStatement = "SELECT * FROM Task WHERE name = ?";
        String[] variables = new String[1];
        variables[0] = name;
        
        String[][] result = dbConn.executeSQLSelectPreparedStatement("", "", selectStatement, variables);
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

        dbConn.executeSQLUpdatePreparedStatement("", "", updateStatement, variables);
    }
    
    public void createTask() {
        String insertStatement = "INSERT INTO Task VALUES('" + this.name + "', '" + this.dbName + "', " + this.dbMem + ", " + this.players + ")";
        
        dbConn.executeSQLUpdateStatement("", "", insertStatement);
    }
    
    public void closeTask() {
        if (this.players == 1) {
            String deleteStatement = "DELETE FROM Task WHERE id = ?";
            String[] variables = new String[1];
            variables[0] = this.name;
            
            dbConn.executeSQLUpdatePreparedStatement("", "", deleteStatement, variables);
        }
    }
    
    public boolean checkTask(String taskName) {
        String selectStatement = "SELECT * FROM Task WHERE name = ?";
        String[] variables = new String[1];
        variables[0] = taskName;
        
        String[][] result = dbConn.executeSQLSelectPreparedStatement("", "", selectStatement, variables);
        return result[0][0] != null;
    }
}
