package de.tu_bs.cs.ifis.sqlgame.sandbox;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.Header;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.MySAXParser;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.XMLSyntaxCheck;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import org.xml.sax.InputSource;

/**
 * Class InputFile.
 *
 * Create tasks for each task in a input xml-file.
 * The file is checked and tasks are only created if the file is correct.
 *
 * @author Tobias Gruenhagen, Philip Holzhueter, Tobias Runge
 */
public class InputFile {

    private String filename;
    private ArrayList<Task> tasks;
    
    private final Config conf = ConfigFactory.load();
    
    /**
     * Getter for tasks.
     * 
     * @return ArrayList list of tasks
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }
    
    /**
     * Setter for tasks.
     * 
     * @param tasks ArrayList list of tasks
     */
    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Constructor InputFile.
     *
     * @param file String, the content of the file
     * @param isFile boolean true if the String is a file name,
     *        false if the String is a file string
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     *         Exception for the parsing of the document
     */
    public InputFile(String file, boolean isFile) throws MySQLAlchemistException {
        String filePath = conf.getString("input.xmlPath");
        File newfile;
        XMLSyntaxCheck sych = new XMLSyntaxCheck();
        MySAXParser msp = new MySAXParser();
        //Proof if a fileString or a file is given
        if (isFile) {
            //A file is given
            this.filename = file;
            //Make the xml-sructure-check
            sych.checkxml(this.filename, true);
            //Parse the xml-file und build the db-tables
            msp.parseDocument(this.filename, true);
            this.tasks = msp.getMyTasks();
        } else {
            //A file string is given
            //Make the xml-sructure-check
            sych.checkxml(file, false);
            //Parse the xml-file und build the db-tables
            msp.parseDocument(file, false);
            this.tasks = msp.getMyTasks();
   
            //Create a new file from the file string
            Iterator<Task> it = this.tasks.iterator();
            if (it.hasNext()) {
                Task task = it.next();
                Iterator<Header> it2 = task.getMyHeader().iterator();
                if (it2.hasNext()) {
                    Header header = it2.next();

                    this.filename = header.getTaskId();
                    String fullFilePath = filePath + this.filename + ".xml";
                    newfile = new File(fullFilePath);
                    if (checkFile(newfile)) {
                        System.out.println(fullFilePath + " erzeugt");
                    }
                    try {
                        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(newfile),"UTF-8")) {
                            out.write(file);
                            out.flush();
                        }
                    } catch (IOException ex) {
                        throw new MySQLAlchemistException("Fehler beim Erstellen der Datei. ", ex);
                    }
                }
            }
        }
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
