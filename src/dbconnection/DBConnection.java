package dbconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Class DB-Connection.
 * 
 * Establishing a connection to a database and executing SQL-Statements.
 * 
 * @author Tobias Grünhagen
 */
public class DBConnection {
    
    private final String driver;
    private final String dbURL;
    
    /**
     * Constructor DBConnection.
     * 
     * Declare some Variables.
     * 
     * @param driver String, JDDB-Driver
     * @param path String, path
     * @param databaseName String, databasename
     */
    public DBConnection(String driver, String path, String databaseName) {
        this.driver = driver;
        this.dbURL = "jdbc:h2:" + path + "/" + databaseName;
    }
    
    /**
     * Method executeSQLStatement.
     * 
     * Building a connection to a database. Executing a SQL-Statement.
     * The ResultSet of a SELECT-Statement is printed out,
     * 
     * @param user String, username
     * @param pass String, password for user
     * @param sqlStatement String, SQL-Statement to be executed
     * @return String[][], multidimensional Stringarray containing
     *                     the name of the DB-table and the associated value
     */
    public String[][] executeSQLStatement(String user, String pass, String sqlStatement) {
        Connection conn = null;
        Statement stmt = null;
        String[][] result = null;
        
        try {
            //Register JDBC driver
            System.out.println("Register driver...");
            Class.forName(this.driver);

            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(this.dbURL, user, pass);

            //Execute a query
            System.out.println("Executing SQL-Statement in given database...");
            stmt = conn.createStatement();
            if (this.checkKindOfStatement(sqlStatement)) {
                ResultSet rs = stmt.executeQuery(sqlStatement);
                //Printing out the ResultSet
                this.printResultSet(rs);
                result = this.transformResultSet(rs);
                //Close ResultSet
                rs.close();
            } else {
                stmt.executeUpdate(sqlStatement);
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
        
        return result;
    }
    
    /**
     * Method executeSQLStatement.
     * 
     * Building a connection to a database. Executing a SQL-Statement.
     * The ResultSet of a SELECT-Statement is printed out,
     * 
     * @param user String, username
     * @param pass String, password for user
     * @param sqlStatement String, SQL-Statement to be executed
     * @return List, List with several multidimensional Stringarrays containing
     *               the name of the DB-table and the associated value
     */
    public List executeSQLStatement(String user, String pass, String[] sqlStatement) {
        Connection conn = null;
        Statement stmt = null;
        List result = new ArrayList();
        
        try {
            //Register JDBC driver
            System.out.println("Register driver...");
            Class.forName(this.driver);

            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(this.dbURL, user, pass);

            //Execute a querys
            stmt = conn.createStatement();
            for (String sqlStmt : sqlStatement) {
                System.out.println("Executing SQL-Statement in given database...");
                if (this.checkKindOfStatement(sqlStmt)) {
                    ResultSet rs = stmt.executeQuery(sqlStmt);
                    //Printing out the ResultSet
                    this.printResultSet(rs);
                    result.add(this.transformResultSet(rs));
                    //Close ResultSet
                    rs.close();
                } else {
                    stmt.executeUpdate(sqlStmt);
                }
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
;
            }
        }
        
        return result;
    }
    
    /**
     * Method checkKindOfStatement.
     * 
     * Checks whether the sql-statement is a SELECT-statement or not.
     * 
     * @param sqlStatement String, SQL-statement
     * @return boolean, true if sql-statment is SELECT-statement
     */
    public boolean checkKindOfStatement(String sqlStatement) {
        boolean isSelect = false;
        
        StringTokenizer st = new StringTokenizer(sqlStatement);
        String s = st.nextToken().toLowerCase();
        if (s.equals("select")) {
            isSelect = true;
        }
        
        return isSelect;
    }
    
    /**
     * Method tranformResultSet.
     * 
     * Transforms a ResultSet into a multidimensional Stringarray.
     * 
     * @param rs ResultSet, ResultSet of a SELECT-statement
     * @return String[][], multidimensional Stringarray containing
     *                     the name of the DB-table and the associated value
     * @throws java.sql.SQLException
     */
    private String[][] transformResultSet(ResultSet rs) throws SQLException {
        String[][] result = null;
        
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        result = new String[columnsNumber][2];
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                result[i][0] = rsmd.getColumnName(i);
                result[i][1] = rs.getString(i);
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
     * @throws java.sql.SQLException
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
