package de.tu_bs.cs.ifis.sqlgame.xmlparse;

<<<<<<< HEAD:src/main/java/xmlparse/MySAXParser.java
import exception.MySQLAlchemistException;
=======
import de.tu_bs.cs.ifis.sqlgame.dbconnection.*;
import de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException;
>>>>>>> 9a687657d36ee48d5b35a46ac1e11b178f83ba03:src/main/java/de/tu_bs/cs/ifis/sqlgame/xmlparse/MySAXParser.java
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.typesafe.config.*;
<<<<<<< HEAD:src/main/java/xmlparse/MySAXParser.java
import dbconnection.DBConnection;
import java.util.Iterator;
import sandbox.Task;
=======
import java.util.logging.Level;
import java.util.logging.Logger;
import de.tu_bs.cs.ifis.sqlgame.sandbox.Task;
>>>>>>> 9a687657d36ee48d5b35a46ac1e11b178f83ba03:src/main/java/de/tu_bs/cs/ifis/sqlgame/xmlparse/MySAXParser.java

/**
 * Class MySAXParser.
 *
 * Class to parse a xml and put the information into java-task-objects.
 *
 * @author Tobias Runge
 */
public class MySAXParser extends DefaultHandler {

    private List myRelation;
    private List myHeader;
    private List myExercise;
    private List myTasks;

    private Header tempHeader;
    private Relation tempRelation;
    private Exercise tempExercise;

    private final StringBuffer sb;

    /**
     * Getter for myRelation.
     *
     * @return List, list with relationinformation
     */
    public List getMyRelation() {
        return this.myRelation;
    }

    /**
     * Getter for myHeader
     *
     * @return List, list with headerinformation
     */
    public List getMyHeader() {
        return this.myHeader;
    }

    /**
     * Getter for myExercise.
     *
     * @return List, list with exerciseinformation
     */
    public List getMyExercise() {
        return this.myExercise;
    }

    /**
     * Getter for myTask.
     *
     * @return List, list with tasks
     */
    public List getMyTasks() {
        return this.myTasks;
    }

    /**
     * Setter for myTask.
     *
     * @param myTasks List, list with tasks
     */
    public void setMyTasks(List myTasks) {
        this.myTasks = myTasks;
    }

    /**
     * Constructor MySAXParser.
     *
     * Create empty lists and define default variables.
     */
    public MySAXParser() {
        this.myRelation = new ArrayList();
        this.myExercise = new ArrayList();
        this.myHeader = new ArrayList();
        this.myTasks = new ArrayList();
        this.sb = new StringBuffer();
    }

    /**
     * Method parseDocument.
     *
<<<<<<< HEAD:src/main/java/xmlparse/MySAXParser.java
     * Parse the XML-File.
     *
     * @param exercise String, the xml-file that is parsed
     * @throws exception.MySQLAlchemistException, exception from parsing the
=======
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
>>>>>>> 9a687657d36ee48d5b35a46ac1e11b178f83ba03:src/main/java/de/tu_bs/cs/ifis/sqlgame/xmlparse/MySAXParser.java
     * document
     */
    public void parseDocument(String exercise) throws MySQLAlchemistException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //New instance of a parser
            SAXParser sp = spf.newSAXParser();
            
            //Parse the file and register this class for call backs
            Config conf = ConfigFactory.load();
            sp.parse(conf.getString("input.xml") + exercise, this);
            
            /* //Check SQL-syntax
            dbconnection.DBConnection dbcnn = new DBConnection(conf.getString("input.fixDb"));
            Iterator it0 = this.myTasks.iterator();
            while (it0.hasNext()) {
                Task t = (Task) it0.next();
                
                //Check CREATE-TABLE and INSERT-INTO statements
                Iterator it1 = t.getMyRelation().iterator();
                while (it1.hasNext()) {
                    Relation r = (Relation) it1.next();
                    dbcnn.checkSQLSyntax(r.getIntension());
                    String[] s = r.getTuple();
                    for (String se : s) {
                        dbcnn.checkSQLSyntax(se);
                    }
                }

                //Check referencestatements
                Iterator it2 = t.getMyExercise().iterator();
                while (it2.hasNext()) {
                    Exercise e = (Exercise) it2.next();
                    dbcnn.checkSQLSyntax(e.getReferencestatement());
                }
            }
            */
        } catch (SAXException | ParserConfigurationException | IOException se) {
            throw new MySQLAlchemistException("Fehler beim Parsen des Dokuments ", se);
        }
    }

    /**
     * Method startElement. Event Handler
     *
     * Helper-method to parse the document, take specific actions at the start
     * of each element.
     *
<<<<<<< HEAD:src/main/java/xmlparse/MySAXParser.java
     * @param uri String, the Namespace URI, or the empty string if the element
     * has no Namespace URI or if Namespace processing is not being performed.
     * @param localName String, the local name (without prefix), or the empty
     * string if Namespace processing is not being performed.
     * @param qName String, the qualified name (with prefix), or the empty
     * string if qualified names are not available.
     * @param attributes The attributes attached to the element. If there are no
     * attributes, it shall be an empty Attributes object.
     * @throws SAXException, exception from parsing the document
=======
     * @param uri 
     * @param localName
     * @param qName
     * @param attributes
     * @throws SAXException
>>>>>>> 9a687657d36ee48d5b35a46ac1e11b178f83ba03:src/main/java/de/tu_bs/cs/ifis/sqlgame/xmlparse/MySAXParser.java
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        this.sb.setLength(0);
        if (qName.equalsIgnoreCase("Relation")) {
            //create a new instance
            this.tempRelation = new Relation();
            //temprelation.setType(attributes.getValue("type"));
        }
        if (qName.equalsIgnoreCase("Task")) {
            this.tempHeader = new Header();
            this.tempHeader.setTaskId(attributes.getValue("taskid"));
        }
        if (qName.equalsIgnoreCase("subtask")) {
            this.tempExercise = new Exercise();
        }
    }

    /**
     * Method characters. Event Handler
     * 
     * Helper-method to parse the document, take specific actions for each chunk
     * of character data.
     *
     * @param ch The characters.
     * @param start The start position in the character array.
     * @param length The number of characters to use from the character array.
     * @throws SAXException, exception from parsing the document
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.sb.append(ch, start, length);
    }

    /**
     * Method endElement. Event Handler
     * 
     * Helper-method to parse the document, take specific actions at the end of
     * each element.
     *
     * @param uri The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @throws SAXException, exception from parsing the document
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("relation")) {
            //add it to the list
            this.myRelation.add(this.tempRelation);
        } else if (qName.equalsIgnoreCase("intension")) {
            String intension = this.sb.toString();
            this.tempRelation.setIntension(intension);
        } else if (qName.equalsIgnoreCase("tuple")) {
            this.tempRelation.setTuple(this.sb.toString());
            
        }

        if (qName.equalsIgnoreCase("subtask")) {
            //add it to the list
            this.myExercise.add(this.tempExercise);
        } else if (qName.equalsIgnoreCase("tasktext")) {
            this.tempExercise.setTasktexts(this.sb.toString());
        } else if (qName.equalsIgnoreCase("referencestatement")) {
            this.tempExercise.setReferencestatement(this.sb.toString());
        } else if (qName.equalsIgnoreCase("evaluationstrategy")) {
            this.tempExercise.setEvaluationstrategy(this.sb.toString());
        } else if (qName.equalsIgnoreCase("term")) {
            this.tempExercise.setTerm(this.sb.toString());
        } else if (qName.equalsIgnoreCase("points")) {
            this.tempExercise.setPoints(Integer.parseInt(this.sb.toString()));
        }

        if (qName.equalsIgnoreCase("task")) {
            //add it to the list
            this.myHeader.add(this.tempHeader);
            this.myTasks.add(new Task(this.tempHeader.getTaskId(), this.myHeader, this.myRelation, this.myExercise));
            this.myHeader = new ArrayList();
            this.myExercise = new ArrayList();
            this.myRelation = new ArrayList();
        } else if (qName.equalsIgnoreCase("title")) {
            this.tempHeader.setTitle(this.sb.toString());
        } else if (qName.equalsIgnoreCase("flufftext")) {
            this.tempHeader.setFlufftext(this.sb.toString());
        }
    }
}
