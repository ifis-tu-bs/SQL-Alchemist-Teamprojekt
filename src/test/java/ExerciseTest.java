/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Philip
 */
import de.tu_bs.cs.ifis.sqlgame.xmlparse.Exercise;
import junit.framework.*;

/**
 * Class ExerciseTest
 *
 * Testclass for the class Exercise
 *
 * @author Tobias
 */
public class ExerciseTest extends TestCase {
    
    public void testTasktexts() {
        Exercise myExercise = new Exercise();
        
        myExercise.setTasktexts("testtext");
        myExercise.setTasktexts("testtext 2");
        myExercise.setTasktexts("testtext 3");
        
        String[] temp = new String[3];
        temp[0] = "testtext";
        temp[1] = "testtext 2";
        temp[2] = "testtext 3";
        
        for(int i = 0; i < temp.length; i++) {
            Assert.assertEquals(myExercise.getTasktexts()[i], temp[i]);
        }
      
    }
    
    public void testReferencestatement() {
        Exercise myExercise = new Exercise();
        
        myExercise.setReferencestatement("teststmt");
        Assert.assertTrue(myExercise.getReferencestatement().equals("teststmt"));
    }

    public void testEvaluationstrategy() {
        Exercise myExercise = new Exercise();
        
        myExercise.setEvaluationstrategy("teststrategy");
        Assert.assertTrue(myExercise.getEvaluationstrategy().equals("teststrategy"));
    }
    
    public void testTerm() {
        Exercise myExercise = new Exercise();
        
        myExercise.setTerm("testterm");
        myExercise.setTerm("testterm 2");
        myExercise.setTerm("testterm 3");
        
        String[] temp = new String[3];
        temp[0] = "testterm";
        temp[1] = "testterm 2";
        temp[2] = "testterm 3";
        
        String s = "";
        for (String temp1 : temp) {
            s += temp1 + "\n";
        }
        
        Assert.assertTrue(myExercise.getTerm().equals(s));
      
    }
    
    public void testPoints() {
        Exercise myExercise = new Exercise();
        
        myExercise.setPoints(5);
        Assert.assertEquals(5, myExercise.getPoints());
    }

    public void testLanguage() {
        Exercise myExercise = new Exercise();
        
        myExercise.setLanguage("testlang");
        Assert.assertEquals("testlang", myExercise.getLanguage());
    }
}
