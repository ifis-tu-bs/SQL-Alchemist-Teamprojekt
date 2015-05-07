package de.tu_bs.cs.ifis.sqlgame.xmlparse;

import de.tu_bs.cs.ifis.sqlgame.dbconnection.*;
import de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

import com.typesafe.config.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.tu_bs.cs.ifis.sqlgame.sandbox.Task;

/**
 * class to parse the XML-File into java
 *
 * @author Tobias
 */
public class MySAXParser extends DefaultHandler {

    private List myrelation;
    private List myheader;
    private List myexercise;
    private List myTasks;

    //to maintain context
    private Header tempheader;
    private Relation temprelation;
    private Exercise tempexercise;
    private Task tempTask;
    
    
    /**
     * Setter for myrelation.
     * 
     * @return List
     */
    public List getMyrelation() {
        return myrelation;
    }
    
    /**
     * Setter for myheader
     * 
     * @return List
     */
    public List getMyheader() {
        return myheader;
    }
    
    /**
     * Setter for myexercise.
     * 
     * @return List
     */
    public List getMyexercise() {
        return myexercise;
    }

    public List getMyTasks() {
        return myTasks;
    }

    public void setMyTasks(List myTasks) {
        this.myTasks = myTasks;
    }
    
    /**
     * buffer for the XML-lines
     */
    StringBuffer sb = new StringBuffer();

    /**
     * Constructor MySAXParser.
     * 
     * Create empty lists.
     */
    public MySAXParser() {
        myrelation = new ArrayList();
        myexercise = new ArrayList();
        myheader = new ArrayList();
        myTasks = new ArrayList();
        
    }

    /**
     * run-method parse the document and print the result
     *
     * @param exercise the xml-file
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception for the parsing of 
     * the document
     */
    public void parseAndCreateDb(String exercise) throws MySQLAlchemistException{
        this.parseDocument(exercise);
        //this.insertToDb();
    }

    /**
     * method to parse the XML-File
     * @param exercise the xml-file that is parsed
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException Exception from parsing the 
     * document
     */
    public void parseDocument(String exercise) throws MySQLAlchemistException {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            Config conf = ConfigFactory.load();
            sp.parse(conf.getString("input.xml") + exercise, this);

        } catch (SAXException | ParserConfigurationException | IOException se) {
            throw new MySQLAlchemistException("Fehler beim Parsen des Dokuments ", se);
        }
    }


    /**
     * Iterate through the list and insert the contents of the xml-file to the
     * database
     * @throws exception.MySQLAlchemistException Exception for the
     * SQLUpdateStatement
     */
    /*public void insertToDb() throws MySQLAlchemistException{
        Iterator it = myrelation.iterator();

        //Database credentials
        String user = "";
        String pass = "";

        while (it.hasNext()) {
            Relation s = (Relation) it.next();
            String[] a = s.getTuple();
            for (int i = 0; i < a.length; i++) {
                a[i] = a[i].replace('\"', '\'');
            }
            this.dbConn.executeSQLUpdateStatement(user, pass, s.getIntension());
            this.dbConn.executeSQLUpdateStatement(user, pass, a);
        }
    }
*/
    /**
     * Iterate through the list and execute the Statements of the xml-file in
     * the database
     * @throws exception.MySQLAlchemistException Exception for the
     * SQLSelectStatement
     */
 /*   public void selectFromDb() throws MySQLAlchemistException{
        Iterator it = myexercise.iterator();

        //Database credentials
        String user = "";
        String pass = "";
        
        while (it.hasNext()) {
            Exercise select = (Exercise) it.next();
            String selectString = select.getReferencestatement().replace('\"', '\'');
            this.dbConn.executeSQLSelectStatement(user, pass, selectString);
        }
    }
*/
    //Event Handlers
    /**
     * Helper-method to parse the document
     *
     * @param uri 
     * @param localName
     * @param qName
     * @param attributes
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        sb.setLength(0);
        if (qName.equalsIgnoreCase("Relation")) {
            //create a new instance
            temprelation = new Relation();
            //temprelation.setType(attributes.getValue("type"));
        }
        if (qName.equalsIgnoreCase("Task")) {
            tempheader = new Header();
            tempheader.setTaskId(attributes.getValue("taskid"));
        }
        if (qName.equalsIgnoreCase("subtask")) {
            tempexercise = new Exercise();
        }
    }

    /**
     * Helper-method to parse the document
     *
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        sb.append(ch, start, length);
    }

    /**
     * Helper-method to parse the document
     *
     * @param uri
     * @param localName
     * @param qName
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("relation")) {
            //add it to the list
            myrelation.add(temprelation);
        } else if (qName.equalsIgnoreCase("intension")) {
            temprelation.setIntension(sb.toString());
        } else if (qName.equalsIgnoreCase("tuple")) {
            temprelation.setTuple(sb.toString());
        }

        if (qName.equalsIgnoreCase("subtask")) {
            //add it to the list
            myexercise.add(tempexercise);
        } else if (qName.equalsIgnoreCase("tasktext")) {
            tempexercise.setTasktexts(sb.toString());
        } else if (qName.equalsIgnoreCase("referencestatement")) {
            tempexercise.setReferencestatement(sb.toString());
        } else if (qName.equalsIgnoreCase("evaluationstrategy")) {
            tempexercise.setEvaluationstrategy(sb.toString());
        } else if (qName.equalsIgnoreCase("term")) {
            tempexercise.setTerm(sb.toString());
        } else if (qName.equalsIgnoreCase("points")) {
            tempexercise.setPoints(Integer.parseInt(sb.toString()));
        }

        if (qName.equalsIgnoreCase("task")) {
            //add it to the list
            myheader.add(tempheader);
            try {
                tempTask = new Task(tempheader.getTaskId(), myheader, myrelation, myexercise);
            } catch (MySQLAlchemistException ex) {
                Logger.getLogger(MySAXParser.class.getName()).log(Level.SEVERE, null, ex);
            }
            myTasks.add(tempTask);
            myheader = new ArrayList();
            myexercise = new ArrayList();
            myrelation = new ArrayList();
        } else if (qName.equalsIgnoreCase("title")) {
            tempheader.setTitle(sb.toString());
        } else if (qName.equalsIgnoreCase("flufftext")) {
            tempheader.setFlufftext(sb.toString());
        }

    }
}
