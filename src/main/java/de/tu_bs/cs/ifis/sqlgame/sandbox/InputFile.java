package de.tu_bs.cs.ifis.sqlgame.sandbox;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.Header;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.MySAXParser;
import de.tu_bs.cs.ifis.sqlgame.xmlparse.XMLSyntaxCheck;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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
     * @return ArrayList<Task> list of tasks
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }
    
    /**
     * Setter for tasks.
     * 
     * @param tasks ArrayList<Task> list of tasks
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
        File newfile = null;
        
        //Proof if a fileString or a file is given
        if (isFile) {
            //A file is given
            this.filename = file;
        } else {
            //A file string is given, so create a new file from it
            this.filename = "newdata.xml";
            String fullFilePath = filePath + this.filename;
            newfile = new File(fullFilePath);
            if (checkFile(newfile)) {
                System.out.println(fullFilePath + " erzeugt");
            }
            try {
                FileWriter writer = new FileWriter(newfile);
                writer.write(file);
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                throw new MySQLAlchemistException("Fehler beim Erstellen der Datei. ", ex);
            }
        }

        //Make the xml-sructure-check
        XMLSyntaxCheck sych = new XMLSyntaxCheck();
        sych.checkxml(this.filename);

        //Parse the xml-file und build the db-tables
        MySAXParser msp = new MySAXParser();
        msp.parseDocument(this.filename);
        this.tasks = msp.getMyTasks();
        
        if (!isFile) {
            //Rename the new file from the file string
            Iterator<Task> it = this.tasks.iterator();
            if (it.hasNext()) {
                Task task = it.next();
                Iterator<Header> it2 = task.getMyHeader().iterator();
                if (it2.hasNext()) {
                    Header header = it2.next();

                    this.filename = header.getTaskId();
                    newfile.renameTo(new File(filePath + this.filename + ".xml"));
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
