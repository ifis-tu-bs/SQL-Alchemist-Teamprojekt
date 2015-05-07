/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sandbox;

import exception.MySQLAlchemistException;
import xmlparse.MySAXParser;
import xmlparse.XMLSyntaxCheck;

/**
 *
 * @author Tobias
 */
public class InputFile {
    
    private String filename;
    
    /**
     * Constructor InputFile.
     * 
     * @param filename String, filenmae without
     * @throws exception.MySQLAlchemistException
     */
    public InputFile(String filename) throws MySQLAlchemistException {
        this.filename = filename;
        
        //Make the xml-sructure-check
        XMLSyntaxCheck sych = new XMLSyntaxCheck();
        sych.checkxml(this.filename + ".xml");

        //Parse the xml-file und build the db-tables
        MySAXParser msp = new MySAXParser();
        msp.parseDocument(this.filename + ".xml");
    }
}
