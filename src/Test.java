import dbconnection.*;
import xmlparse.*;
import java.sql.*;

/**
 * Class Test.
 * 
 * Testing an Presenting the actual projectstatus.
 * 
 * @author Tobias Grünhagen
 */
public class Test {
    
    /**
     * Main Method.
     * 
     * @param args unused 
     */
    public static void main(String[] args) {
        SAXParserExample spe = new SAXParserExample();
	spe.runExample();
        
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String serverName = "localhost";
        String databaseName = "sql-alchemist-teamprojekt";
        
        //Database credentials
        String user = "root";
        String pass = "123";
        
        String sqlStatement = "CREATE TABLE REGISTRATION " +
            "(id INTEGER not NULL, " +
            " first VARCHAR(255), " + 
            " last VARCHAR(255), " + 
            " age INTEGER, " + 
            " PRIMARY KEY ( id ))";
        String sqlStatement2 = "INSERT INTO Registration " +
                   "VALUES(102, 'Sumit', 'Mittal', 23)";
        String sqlStatement3 = "SELECT * FROM REGISTRATION";
        
        DBConnection dbConnection = new DBConnection(jdbcDriver, serverName, databaseName);
        Connection conn = dbConnection.buildDBConnection(user, pass);
        dbConnection.executeSQLStatement(sqlStatement3, conn);
        dbConnection.closeDBConnection(conn);
    }
}
