import xmlparse.*;

/**
 * Class Test.
 * 
 * Testing an Presenting the actual projectstatus.
 * 
 * @author Tobias Grünhagen
 */
public class Test {
    
    /**
     * Constructor Test.
     */
    public Test() {
        //Nothing to do.
    }
    
    /**
     * Main Method.
     * 
     * @param args unused 
     */
    public static void main(String[] args) {
        //Pass the xml-file
        String exercise = "exercises-wise13.xml";
        
        //Make the xml-sructure-check
        XMLSyntaxCheck sych = new XMLSyntaxCheck();
        sych.checkxml(exercise);
        
        //Parse the xml-file und uild the db-tables
        MySAXParser sp = new MySAXParser();
        sp.runExample(exercise);
    }
}
