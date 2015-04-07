import java.sql.*;

package dbconnection;

/**
 * Class DB-Connection.
 * 
 * Establishing a connection to a database and executing SQL-Statements.
 * 
 * @author Tobias Grünhagen
 */
public class DBConnection {
    
    private String jdbcDriver;
    private String serverName;
    private String databaseName;
    private String dbURL;
    
    /**
     * Constructor DBConnection.
     * 
     * Declare some Variables.
     * 
     * @param jdbcDriver String, JDDB-Driver
     * @param serverName String, servername
     * @param databaseName String, databasename
     */
    public DBConnection(String jdbcDriver, String serverName, String databaseName) {
        this.jdbcDriver = jdbcDriver;
        this.serverName = serverName;
        this.databaseName = databaseName;
        
        this.dbURL = "jdbc:mysql://" + serverName + "/" + databaseName;
    }
    
    /**
     * Method buildDBConnection.
     * 
     * Building a Connection to a Database, the connection is returned to
     * execute SQL-Statements on this connection.
     * 
     * @param user String, username
     * @param pass String, password for user
     * @return Connection, DB-Connection
     */
    public Connection buildDBConnection(String user, String pass) {
        Connection conn = null;
        
        try {
            //Register JDBC driver
            System.out.println("Register JDBC-Driver...");
            Class.forName(this.jdbcDriver);

            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(this.dbURL, user, pass);
        } catch (SQLException se) {
           //Handle errors for JDBC
           se.printStackTrace();
        } catch(Exception e) {
           //Handle errors for Class.forName
           e.printStackTrace();
        }
        
        return conn;
    }
    
    
    /**
     * Method closeDBConnection.
     * 
     * Closing DB-Connection.
     * 
     * @param conn Connection, DB-Connection
     */
    public void closeDBConnection(Connection conn) {
        try {
            //Close connection
            System.out.println("Closing Connection...");
            conn.close();
        } catch (SQLException se) {
           //Handle errors for JDBC
           se.printStackTrace();
        } catch(Exception e) {
           //Handle errors for Class.forName
           e.printStackTrace();
        }
    }
    
    /**
     * Method executeSQLStatement.
     * 
     * Executing any SQL-Statement. The ResultSet is null
     * if the SQL-Statement is not a SELECT-Statement. Otherwise the ResultSet
     * of the SELECT-Statement is printed out,
     * 
     * @param sqlStatement String, SQL-Statement to be executed
     * @param conn Connection, DB-Connecion
     * @param select Boolean, true if SQL-Statement is Select-Statement
     */
    public void executeSQLStatement(String sqlStatement, Connection conn, boolean select) {
        Statement stmt = null;
        
        try {
            //Execute a query
            System.out.println("Executing SQL-Statement in given database...");
            stmt = conn.createStatement();
            
            if (select) {
                ResultSet rs = stmt.executeQuery(sqlStatement);
                //Printing out the ResultSet
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= columnsNumber; i++) {
                        if (i > 1) {
                            System.out.print(",  ");
                        }
                        System.out.print(rs.getString(i) + " " + rsmd.getColumnName(i));
                    }
                    System.out.println("");
                }
            } else {
                stmt.executeUpdate(sqlStatement);
                //Close statement
                stmt.close();
            }
            System.out.println("SQL-Statement executed...");
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt!=null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
                se2.printStackTrace();
            }
            try {
                if (conn!=null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
    
    /**
     * Main-Methde for testing, can be deleted if not longer needed.
     * 
     * @param args not used
     */
    public static void main(String[] args) {
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String serverName = "localhost";
        String databaseName = "sql-alchemist-teamprojekt";
        
        //Database credentials
        String user = "root";
        String pass = "";
        
        String sqlStatement = "CREATE TABLE REGISTRATION " +
            "(id INTEGER not NULL, " +
            " first VARCHAR(255), " + 
            " last VARCHAR(255), " + 
            " age INTEGER, " + 
            " PRIMARY KEY ( id ))";
        String sqlStatement2 = "INSERT INTO Registration " +
                   "VALUES(10, 'Sumit', 'Mittal', 23)";
        String sqlStatement3 = "SELECT * FROM REGISTRATION";
        
        DBConnection dbConnection = new DBConnection(jdbcDriver, serverName, databaseName);
        Connection conn = dbConnection.buildDBConnection(user, pass);
        dbConnection.executeSQLStatement(sqlStatement3, conn, true);
        dbConnection.buildDBConnection(user, pass);
    }
}
