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
        XMLSyntaxCheck sych = new XMLSyntaxCheck();
        try {
            sych.checkxml("exercises-wise11.xml");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        SAXParserExample spe = new SAXParserExample();
	spe.runExample();
    }
}
