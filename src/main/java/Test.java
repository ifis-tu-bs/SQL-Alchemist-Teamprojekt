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
        sandbox.Task test1 = new sandbox.Task("alchemy-task", "alchemy-task");
        test1.startTask();
        test1.closeTask();
        
        sandbox.Task test2 = new sandbox.Task("exam-wise13", "exam-wise13");
        test2.startTask();
        test2.closeTask();
        
        sandbox.Task test3 = new sandbox.Task("exercises-wise11", "exercises-wise11");
        test3.startTask();
        test3.closeTask();
        
        sandbox.Task test4 = new sandbox.Task("exercises-wise13", "exercises-wise13");
        test4.startTask();
        test4.closeTask();
    }
}
