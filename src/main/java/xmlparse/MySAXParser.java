package xmlparse;

import dbconnection.*;
import exception.MySQLAlchemistException;
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

/**
 * class to parse the XML-File into java
 *
 * @author Tobias
 */
public class MySAXParser extends DefaultHandler {

    private List myrelation;
    private List myheader;
    private List myexercise;

    //to maintain context
    private Header tempheader;
    private Relation temprelation;
    private Exercise tempexercise;
    
    private DBConnection dbConn;
    
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
    
    /**
     * buffer for the XML-lines
     */
    StringBuffer sb = new StringBuffer();

    /**
     * Constructor MySAXParser.
     * 
     * Create empty lists.
     * 
     * @param dbConn
     */
    public MySAXParser(DBConnection dbConn) {
        myrelation = new ArrayList();
        myexercise = new ArrayList();
        myheader = new ArrayList();
        
        this.dbConn = dbConn;
    }

    /**
     * run-method parse the document and print the result
     *
     * @param exercise the xml-file
     * @throws exception.MySQLAlchemistException Exception for the parsing of 
     * the document
     */
    public void parseAndCreateDb(String exercise) throws MySQLAlchemistException{
        this.parseDocument(exercise);
        this.insertToDb();
    }

    /**
     * method to parse the XML-File
     * @param exercise the xml-file that is parsed
     * @throws exception.MySQLAlchemistException Exception from parsing the 
     * document
     */
    public void parseDocument(String exercise) throws MySQLAlchemistException {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("input/xml/" + exercise, this);

        } catch (SAXException | ParserConfigurationException | IOException se) {
            throw new MySQLAlchemistException("Fehler beim Parsen des Dokuments ", se);
        }
    }

    /**
     * Iterate through the list and print the contents of the xml-file
     */
    public void printData() {

        System.out.println("No of Relations '" + myrelation.size() + "'.");

        Iterator it = myrelation.iterator();

        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }

        System.out.println("No of Exercises '" + myexercise.size() + "'.");

        it = myexercise.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        System.out.println("No of Header '" + myheader.size() + "'.");

        it = myheader.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    /**
     * Iterate through the list and insert the contents of the xml-file to the
     * database
     * @throws exception.MySQLAlchemistException Exception for the
     * SQLUpdateStatement
     */
    public void insertToDb() throws MySQLAlchemistException{
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

    /**
     * Iterate through the list and execute the Statements of the xml-file in
     * the database
     * @throws exception.MySQLAlchemistException Exception for the
     * SQLSelectStatement
     */
    public void selectFromDb() throws MySQLAlchemistException{
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
        } else if (qName.equalsIgnoreCase("title")) {
            tempheader.setTitle(sb.toString());
        } else if (qName.equalsIgnoreCase("flufftext")) {
            tempheader.setFlufftext(sb.toString());
        }

    }
}
