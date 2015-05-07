/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_bs.cs.ifis.sqlgame.sandbox;

import de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException;
import java.util.List;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.MySAXParser;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.XMLSyntaxCheck;

/**
 *
 * @author Tobias
 */
public class InputFile {
    
    private String filename;
    private List tasks;

    public List getTasks() {
        return tasks;
    }

    public void setTasks(List tasks) {
        this.tasks = tasks;
    }
    
    
    /**
     * Constructor InputFile.
     * 
     * @param filename String, filenmae without
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException throws MySQLAlchemistException
     */
    public InputFile(String filename) throws MySQLAlchemistException {
        this.filename = filename;
        
        //Make the xml-sructure-check
        XMLSyntaxCheck sych = new XMLSyntaxCheck();
        sych.checkxml(this.filename + ".xml");

        //Parse the xml-file und build the db-tables
        MySAXParser msp = new MySAXParser();
        msp.parseDocument(this.filename + ".xml");
        this.tasks = msp.getMyTasks();
    }
}