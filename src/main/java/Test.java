import sandbox.*;
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
     * Method main.
     * 
     * @param args unused 
     */
    public static void main(String[] args) {
        Game test = new Game();
        sandbox.Task task = test.startGame("test");
        test.endGame(task);
    }
}
