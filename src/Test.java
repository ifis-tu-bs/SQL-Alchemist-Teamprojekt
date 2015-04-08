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
     * Main Method.
     * 
     * @param args unused 
     */
    public static void main(String[] args) {
        
        String exercise = args[0];
        XMLSyntaxCheck sych = new XMLSyntaxCheck();

            sych.checkxml(exercise);
            
            MySAXParser sp = new MySAXParser();
            sp.runExample(exercise);
    }
}
