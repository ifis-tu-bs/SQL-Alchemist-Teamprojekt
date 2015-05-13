package de.tu_bs.cs.ifis.sqlgame.sandbox;

import de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException;
import java.util.List;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.MySAXParser;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.XMLSyntaxCheck;

/**
 * Class InputFile
 *
 * Create tasks for each task in a input xml-file.
 * The file is checked and tasks
 * are only created, if the file is correct.
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
     * @param filename String, filename without .xml
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     * Exception for the parsing of the document
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
