import sandbox.*;

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
        
        sandbox.Task task1 = test.startGame("alchemy-task");
        task1.getMySaxParser().selectFromDb();
        test.endGame(task1);
        
        sandbox.Task task2 = test.startGame("exam-wise13");
        task2.getMySaxParser().selectFromDb();
        test.endGame(task2);
        
        sandbox.Task task3 = test.startGame("exercises-wise11");
        task3.getMySaxParser().selectFromDb();
        test.endGame(task3);
        
        sandbox.Task task4 = test.startGame("exercises-wise13");
        task4.getMySaxParser().selectFromDb();
        test.endGame(task4);
    }
}
