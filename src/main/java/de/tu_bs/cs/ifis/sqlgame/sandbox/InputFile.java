package de.tu_bs.cs.ifis.sqlgame.sandbox;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException;
import java.util.List;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.MySAXParser;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.XMLSyntaxCheck;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
     * @param file String, the content of the file
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     * Exception for the parsing of the document
     */
    public InputFile(String file) throws MySQLAlchemistException {
        
        Config conf = ConfigFactory.load();
        String path = conf.getString("input.xmlPath");

        String dat = path + "newdata.xml";
        File newfile = new File(dat);
        if (checkFile(newfile)) {
            System.out.println(dat + " erzeugt");
        }
        try {
            FileWriter writer = new FileWriter(newfile);
            writer.write(file);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            throw new MySQLAlchemistException("Fehler beim Erstellen der Datei. ", ex);
        }

        //Make the xml-sructure-check
        XMLSyntaxCheck sych = new XMLSyntaxCheck();
        sych.checkxml("newdata.xml");

        //Parse the xml-file und build the db-tables
        MySAXParser msp = new MySAXParser();
        msp.parseDocument("newdata.xml");
        this.tasks = msp.getMyTasks();
    }
    
    /**
	 * check if the file can be created
	 * 
	 * @param file
	 *            the file that should be created
	 * @return boolean if it was possible
	 */
    private boolean checkFile(File file) throws MySQLAlchemistException {
        if (file != null) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                throw new MySQLAlchemistException("Fehler beim Erstellen der Datei. ", ex);
            }
            if (file.isFile() && file.canWrite() && file.canRead()) {
                return true;
            }
        }
        return false;
    }
}
