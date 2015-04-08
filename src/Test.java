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
        String exercise = "exercises-wise13.xml";
        XMLSyntaxCheck sych = new XMLSyntaxCheck();
        try {
            sych.checkxml(exercise);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        SAXParserExample spe = new SAXParserExample();
	spe.runExample(exercise);
    }
}
