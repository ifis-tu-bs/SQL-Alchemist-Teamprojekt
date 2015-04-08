import dbconnection.*;
import xmlparse.*;
import java.sql.*;
import java.util.List;

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
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String serverName = "localhost";
        String databaseName = "sql-alchemist-teamprojekt";
        
        //Database credentials
        String user = "root";
        String pass = "123";
        
        XMLSyntaxCheck sych = new XMLSyntaxCheck();
        try {
            sych.checkxml("exercises-wise11.xml");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        String sql1 = "SELECT * FROM example_innodb";
        
        String sql2 = "SELECT * FROM example";
        
        String[] sql = new String[2];
        
        sql[0] = sql1;
        sql[1] = sql2;
        
        DBConnection dbconn = new DBConnection("com.mysql.jdbc.Driver", "localhost", "sql-alchemist-teamprojekt");
        List result = dbconn.executeSQLStatement(user, pass, sql);
    }
}
