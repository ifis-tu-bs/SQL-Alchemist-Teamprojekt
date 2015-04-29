/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import xmlparse.Header;
import junit.framework.*;
 /*
 * @author Philip
 */
public class HeaderTest extends TestCase {

    public void testTitle() {
        Header myHeader = new Header();
        
        myHeader.setTitle("testtitle");
        myHeader.setTitle("testtitle 2");
        myHeader.setTitle("testtitle 3");
        
        String[] temp = new String[3];
        temp[0] = "testtitle";
        temp[1] = "testtitle 2";
        temp[2] = "testtitle 3";
        
        for(int i = 0; i < temp.length; i++) {
            Assert.assertEquals(myHeader.getTitle()[i], temp[i]);
        }
    }
    
    public void testFlufftext() {
        Header myHeader = new Header();
        
        myHeader.setFlufftext("testtext");
        myHeader.setFlufftext("testtext 2");
        myHeader.setFlufftext("testtext 3");
        
        String[] temp = new String[3];
        temp[0] = "testtext";
        temp[1] = "testtext 2";
        temp[2] = "testtext 3";
        
        for(int i = 0; i < temp.length; i++) {
            Assert.assertEquals(myHeader.getFlufftext()[i], temp[i]);
        }
    }
       
    public void testLanguage() {
        Header myHeader = new Header();
        
        myHeader.setLanguage("testlang");
        Assert.assertTrue(myHeader.getLanguage().equals("testlang"));
    }
}
   

