import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dbconnection.DBConnection;
import exception.MySQLAlchemistException;
import java.util.Iterator;
import sandbox.*;

/**
 * Class Test.
 * 
 * Testing an presenting the actual projectstatus.
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
        try {

            InputFile test = new InputFile("exercises-wise11");
            /*Iterator it = test.getTasks().iterator();
        
            while (it.hasNext()) {
                Task task = (Task) it.next();
                task.startTask();
                task.printData();
                //task.insertToDb();
                //task.closeTask();
            }

            /*Task test1 = new Task("alchemy-task", "alchemy-task");
            test1.startTask();
            
            test1.closeTask();

            Task test2 = new Task("exam-wise13", "exam-wise13");
            test2.startTask();
            test2.closeTask();

            Task test3 = new Task("exercises-wise11", "exercises-wise11");
            test3.startTask();
            test3.closeTask();

            Task test4 = new Task("exercises-wise13", "exercises-wise13");
            test4.startTask();
            test4.closeTask();*/
        } catch (MySQLAlchemistException mse) {
            System.out.println(mse.getMyMessage());
        }
    }
}
