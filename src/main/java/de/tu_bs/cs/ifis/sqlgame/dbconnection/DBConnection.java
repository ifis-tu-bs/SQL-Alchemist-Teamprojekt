package de.tu_bs.cs.ifis.sqlgame.dbconnection;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException;
import java.sql.*;

/**
 * Class DBConnection.
 *
 * Establishing a connection to a database and giving the possibility to execute
 * SQL-Statements.
 *
 * @author Tobias Gruenhagen
 */
public class DBConnection {
    
    private int dbType;
    private String dbURL;
    
    private final Config conf;
    
    /**
     * Getter for dbType.
     * 
     * @return String, db-type
     */
    public int getDbType() {
        return dbType;
    }
    
    /**
     * Setter for dbType.
     * 
     * @param dbType String, db-type
     */
    public void setDbType(int dbType) {
        this.dbType = dbType;
    }
    
    /**
     * Getter for dbURL.
     * 
     * @return String, db-url
     */
    public String getDbURL() {
        return dbURL;
    }
    
    /**
     * Setter for dbURL.
     * 
     * @param dbURL String, db-url
     */
    public void setDbURL(String dbURL) {
        this.dbURL = dbURL;
    }

    /**
     * Constructor DBConnection.
     *
     * Declare db-url and register jdbc-driver.
     *
     * @param dbType int, 0-local DB, 1-server DB, 2-in-memory DB
     * @param dbPath String, path, where the db is located
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     */
    public DBConnection(String dbType, String dbPath) throws MySQLAlchemistException {
        this.conf = ConfigFactory.load();
        
        String dbTypeH2 = "";
        switch (dbType) {
            case "local": {
                dbTypeH2 = "";
            }
            case "server": {
                //TODO: add server-IP
                dbTypeH2 = "";
            }
            case "memory": {
                dbTypeH2 = "mem:";
            }
        }
        
        this.dbURL = "jdbc:h2:" + dbTypeH2 + dbPath;
        
        if (dbType.equals("memory")) {
            this.dbURL = this.dbURL + ";DB_CLOSE_DELAY=-1";
        }
        
        try {
            Class.forName(this.conf.getString("input.driver"));
        } catch (ClassNotFoundException ex) {
            //Handle errors for Class.forName
            throw new MySQLAlchemistException("Fehler beim Registrieren des Datenbanktreibers (Class.forName())! ", ex);
        }
    }

    /**
     * Method executeSQLSelectStatement.
     *
     * Building a connection to a database. Executing a SQL-Statement. The
     * ResultSet of a SELECT-Statement is returned.
     *
     * @param user String, username
     * @param pass String, password for user
     * @param sqlStatement String, SQL-Statement to be executed
     * @return String[][], multidimensional Stringarray containing the name of
     * the DB-table and the associated value
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the
     * SQLSelectStatement
     */
    public String[][] executeSQLSelectStatement(String user, String pass, String sqlStatement) throws MySQLAlchemistException {
        Connection conn = null;
        Statement stmt = null;
        String[][] result = null;

        try {
            //Open connection
            conn = DriverManager.getConnection(this.dbURL, user, pass);

            //Execute a query
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStatement);
            result = this.transformResultSet(rs);
            
            //Close db-connection
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            throw new MySQLAlchemistException("Fehler beim Ausführen vom SQL-SELECT-Statement ", se);
        }

        return result;
    }

    /**
     * Method executeSQLUpdateStatement.
     *
     * Building a connection to a database. Executing a SQL-Statement and
     * updating the database.
     *
     * @param user String, username
     * @param pass String, password for user
     * @param sqlStatement String, SQL-Statement to be executed
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the
     * SQLUpdateStatement
     */
    public void executeSQLUpdateStatement(String user, String pass, String sqlStatement) throws MySQLAlchemistException {
        Connection conn = null;
        Statement stmt = null;

        try {
            //Open connection
            conn = DriverManager.getConnection(this.dbURL, user, pass);

            //Execute a query
            stmt = conn.createStatement();
            stmt.executeUpdate(sqlStatement);
            
            //Close db-connection
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            throw new MySQLAlchemistException("Fehler beim Ausführen vom SQL-UPDATE-Statement ", se);
        }
    }

    /**
     * Method executeSQLUpdateStatement.
     *
     * Building a connection to a database. Executing a SQL-Statement and
     * updating the database.
     *
     * @param user String, username
     * @param pass String, password for user
     * @param sqlStatement String[], SQL-Statements to be executed
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the
     * SQLUpdateStatement
     */
    public void executeSQLUpdateStatement(String user, String pass, String[] sqlStatement) throws MySQLAlchemistException {
        Connection conn = null;
        Statement stmt = null;

        try {
            //Open connection
            conn = DriverManager.getConnection(this.dbURL, user, pass);

            //Execute queries
            stmt = conn.createStatement();
            for (String sqlStmt : sqlStatement) {
                stmt.executeUpdate(sqlStmt);
            }
            
            //Close db-connection
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            throw new MySQLAlchemistException("Fehler beim Ausführen vom SQL-UPDATE-Statement ", se);
        }
    }
    
    /**
     * Method executeSQLSelectPreparedStatement.
     * 
     * Building a connection to a database. Executing a SQL-PreparedStatement.
     * The ResultSet of a SELECT-Statement is returned.
     * 
     * @param user String, username
     * @param pass String, password for user
     * @param preparedSqlStatement String, SQL-PreparedStatement to be executed
     * @param variables String[], Variables to be passed in the prepared Statement
     * @return String[][], multidimensional Stringarray containing
     *                     the name of the DB-table and the associated value
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the
     * SQLSelectStatement
     */
    public String[][] executeSQLSelectPreparedStatement(String user, String pass, String preparedSqlStatement, String[] variables) throws MySQLAlchemistException {
        Connection conn;
        PreparedStatement pStmt;
        String[][] result = null;
        
        try {
            //Open connection
            conn = DriverManager.getConnection(this.dbURL, user, pass);

            //Execute a query
            pStmt = conn.prepareStatement(preparedSqlStatement);
            for (int i = 1; i <= variables.length; i++) {
                pStmt.setString(i, variables[i-1]);
            }
            ResultSet rs = pStmt.executeQuery();
            result = this.transformResultSet(rs);
            
            //Close db-Connection
            rs.close();
            pStmt.close();
            conn.close();
        } catch (SQLException se) {
            throw new MySQLAlchemistException("Fehler beim Ausführen vom SQL-Statement ", se);
        }
        
        return result;
    }
    
    /**
     * Method executeSQLPreparedStatement.
     * 
     * Building a connection to a database. Executing a SQL-PreparedStatement
     * and updating the database.
     * 
     * @param user String, username
     * @param pass String, password for user
     * @param preparedSqlStatement String, SQL-PreparedStatement to be executed
     * @param variables String[], Variables to be passed in the
     *                            prepared Statement
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the
     * SQLUpdateStatement
     */
    public void executeSQLUpdatePreparedStatement(String user, String pass, String preparedSqlStatement, String[] variables) throws MySQLAlchemistException {
        Connection conn;
        PreparedStatement pStmt;
        
        try {
            //Open connection
            conn = DriverManager.getConnection(this.dbURL, user, pass);

            //Execute a query
            pStmt = conn.prepareStatement(preparedSqlStatement);
            for (int i = 1; i <= variables.length; i++) {
                pStmt.setString(i, variables[i-1]);
            }
            pStmt.executeUpdate();
            
            //Close db-Connection
            pStmt.close();
            conn.close();
        } catch (SQLException se) {
            throw new MySQLAlchemistException("Fehler beim Ausführen vom SQL-Statement ", se);
        }
    }

    /**
     * Method tranformResultSet.
     *
     * Transforms a ResultSet into a multidimensional Stringarray.
     *
     * @param rs ResultSet, ResultSet of a SELECT-statement
     * @return String[][], multidimensional Stringarray containing the name of
     *                     the DB-table and the associated value
     * @throws java.sql.SQLException, SQLException se
     */
    private String[][] transformResultSet(ResultSet rs) throws SQLException {
        String[][] result;

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        result = new String[columnsNumber][2];
        while (rs.next()) {
            for (int i = 0; i < columnsNumber; i++) {
                result[i][0] = rsmd.getColumnName(i+1);
                result[i][1] = rs.getString(i+1);
            }
        }

        return result;
    }

    /**
     * Method printResultSet.
     *
     * Prints out the ResultSet of a SELECT-statement.
     *
     * @param rs ResultSet, ResultSet of a SELECT-statement
     * @throws java.sql.SQLException, SQLException se
     */
    private void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) {
                    System.out.print(",  ");
                }
                System.out.print(rsmd.getColumnName(i) + ": " + rs.getString(i));
            }
            System.out.println("");
        }
    }
}
