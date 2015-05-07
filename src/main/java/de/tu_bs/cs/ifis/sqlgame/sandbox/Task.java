package de.tu_bs.cs.ifis.sqlgame.sandbox;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.tu_bs.cs.ifis.sqlgame.dbconnection.*;
import de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException;
import java.util.Iterator;
import java.util.List;
import org.h2.tools.DeleteDbFiles;
<<<<<<< HEAD:src/main/java/sandbox/Task.java
import xmlparse.Exercise;
import xmlparse.Relation;
=======
import de.tu_bs.cs.ifis.sqlgame.xmlparse.Exercise;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.Header;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.MySAXParser;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.Relation;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.XMLSyntaxCheck;
>>>>>>> 9a687657d36ee48d5b35a46ac1e11b178f83ba03:src/main/java/de/tu_bs/cs/ifis/sqlgame/sandbox/Task.java

/**
 * Class Task.
 *
 * Manage a task (create task, close task, update DB, load from DB, check
 * existence)
 *
 * @author Tobias
 */
public class Task {

    private String name = "";
    private String dbName = "";
    private int players = 0;

    private List myHeader;
    private List myRelation;
    private List myExercise;
    
    private DBConnection tmpDbConn;
    private DBConnection fixDbConn;
    
    private final Config conf = ConfigFactory.load();

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

    public List getMyHeader() {
        return myHeader;
    }

    public void setMyHeader(List myHeader) {
        this.myHeader = myHeader;
    }

    public List getMyRelation() {
        return myRelation;
    }

    public void setMyRelation(List myRelation) {
        this.myRelation = myRelation;
    }

    public List getMyExercise() {
        return myExercise;
    }

    public void setMyExercise(List myExercise) {
        this.myExercise = myExercise;
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
     * Constructor Task.
     *
     * @param name String, name of the task
<<<<<<< HEAD:src/main/java/sandbox/Task.java
     * @param dbName String, name of the db
=======
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the new database
     * connection
>>>>>>> 9a687657d36ee48d5b35a46ac1e11b178f83ba03:src/main/java/de/tu_bs/cs/ifis/sqlgame/sandbox/Task.java
     */
    
    public Task(String name, List myHeader, List myRelation, List myExercise) {
        this.name = name;
        this.dbName = name;
        this.myHeader = myHeader;
        this.myRelation = myRelation;
        this.myExercise = myExercise;

    }

    /**
     * Method loadTask.
     *
     * Load a task and its properties from db depending on the taskname. Save
     * the properties in the local attributes.
     *
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the
     * SQLSelectStatement
     */
    public void loadTask() throws MySQLAlchemistException {
        String selectStatement = "SELECT * FROM Task WHERE name = ?";
        String[] variables = new String[1];
        variables[0] = this.name;

        String[][] result = fixDbConn.executeSQLSelectPreparedStatement(this.conf.getString("auth.user"), this.conf.getString("auth.pass"), selectStatement, variables);
        this.name = result[0][1];
        this.dbName = result[1][1];
        this.players = Integer.parseInt(result[2][1]);
    }

    /**
     * Method updateTask.
     *
     * Update the properties of the given task in the db with the values of the
     * local attributes.
     *
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the
     * SQLUpdateStatement
     */
    public void updateTask() throws MySQLAlchemistException {
        String[] variables = new String[3];
        variables[0] = this.dbName;
        variables[1] = "" + this.players;
        variables[2] = this.name;
        String updateStatement = "UPDATE Task SET db_name = ?, players = ? WHERE name = ?";

        fixDbConn.executeSQLUpdatePreparedStatement(this.conf.getString("auth.user"), this.conf.getString("auth.pass"), updateStatement, variables);
    }

    /**
     * Method startTask.
     *
     * Start a new task with a given filename. It is now checked whether the
     * task already exists or not. If there is a task, the taskoptions are
     * loaded. If not, a new task and a new db is created.
     *
     * @return Task, loaded or created task
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the new database
     * or the xml syntax check
     */
    public Task startTask() throws MySQLAlchemistException {
        try{
            this.fixDbConn = new DBConnection(this.conf.getString("input.fixDb"));
            if (this.checkTask()) {
                this.loadTask();

                //Set db for task
                String dbUrl = this.conf.getString("input.driverDbs") + this.dbName;
                this.tmpDbConn = new DBConnection(dbUrl);



                //Update #players
                int playerNum = this.getPlayers() + 1;
                this.setPlayers(playerNum);

                this.updateTask();
            } else {
                this.players = 1;
                this.createTask();
            }
        }catch(MySQLAlchemistException se){
            closeTask();
            throw new MySQLAlchemistException("Fehler beim starten der Task ", se);
        }

        return this;
    }

    /**
     * Method createTask.
     *
     * Insert a new task-entry into the db and build up a new dbconnection.
     *
     * @throws MySQLAlchemistException Exception for the new database connection
     */
    private void createTask() throws MySQLAlchemistException {
        try{
        String insertStatement = "INSERT INTO Task VALUES('" + this.name + "', '" + this.dbName + "', " + this.players + ")";

        this.fixDbConn.executeSQLUpdateStatement(this.conf.getString("auth.user"), this.conf.getString("auth.pass"), insertStatement);

        //Set db for task
        String dbUrl = this.conf.getString("input.driverDbs") + this.dbName;
        this.tmpDbConn = new DBConnection(dbUrl);
        
        }catch(MySQLAlchemistException se){
            throw new MySQLAlchemistException("Fehler beim erstellen der Task ", se);
        }
    }

    /**
     * Method closeTask.
     *
     * Close a task and increment the number of players playing the given task.
     * If it was the last player that played the task, the taskentry in the db
     * is deleted.
     *
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the
     * SQLUpdateStatement
     */
    public void closeTask() throws MySQLAlchemistException {
        int playerNum = this.players - 1;
        this.players = playerNum;
        this.updateTask();

        //Delete taskentry in DB if #players = 0
        if (this.players == 0) {
            String deleteStatement = "DELETE FROM Task WHERE name = ?";
            String[] variables = new String[1];
            variables[0] = this.name;

            //Delete the database for the task
            DeleteDbFiles.execute(this.conf.getString("input.dbs"), this.dbName, true);

            fixDbConn.executeSQLUpdatePreparedStatement(this.conf.getString("auth.user"), this.conf.getString("auth.pass"), deleteStatement, variables);
        }
    }

    /**
     * Method checkTask.
     *
     * Check whether the given task already exists in the db or not.
     *
     * @return boolean, true if the given task exist
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the
     * SQLSelectStatement
     */
    public boolean checkTask() throws MySQLAlchemistException {
        String selectStatement = "SELECT * FROM Task WHERE name = ?";
        String[] variables = new String[1];
        variables[0] = this.name;

        String[][] result = fixDbConn.executeSQLSelectPreparedStatement(this.conf.getString("auth.user"), this.conf.getString("auth.pass"), selectStatement, variables);
        return result[0][0] != null;
    }
    
    /**
     * Iterate through the list and insert the contents of the xml-file to the
     * database
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the
     * SQLUpdateStatement
     */
    public void insertToDb() throws MySQLAlchemistException{
        Iterator it = myRelation.iterator();

        while (it.hasNext()) {
            Relation s = (Relation) it.next();
            String[] a = s.getTuple();
            for (int i = 0; i < a.length; i++) {
                a[i] = a[i].replace('\"', '\'');
            }
            this.fixDbConn.executeSQLUpdateStatement(this.conf.getString("auth.user"), this.conf.getString("auth.pass"), s.getIntension());
            this.fixDbConn.executeSQLUpdateStatement(this.conf.getString("auth.user"), this.conf.getString("auth.pass"), a);
        }
    }

    /**
     * Iterate through the list and execute the Statements of the xml-file in
     * the database
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the
     * SQLSelectStatement
     */
    public void selectFromDb() throws MySQLAlchemistException{
        Iterator it = myExercise.iterator();
        
        while (it.hasNext()) {
            Exercise select = (Exercise) it.next();
            String selectString = select.getReferencestatement().replace('\"', '\'');
            this.fixDbConn.executeSQLSelectStatement(this.conf.getString("auth.user"), this.conf.getString("auth.pass"), selectString);
        }
    }
    
     /**
     * Iterate through the list and print the contents of the xml-file
     */
    public void printData() {

        System.out.println("No of Relations '" + myRelation.size() + "'.");

        Iterator it = myRelation.iterator();

        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }

        System.out.println("No of Exercises '" + myExercise.size() + "'.");

        it = myExercise.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        System.out.println("No of Header '" + myHeader.size() + "'.");

        it = myHeader.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

}
