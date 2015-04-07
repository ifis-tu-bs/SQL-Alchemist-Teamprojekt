package xmlparse;

import dbconnection.DBConnection;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserExample extends DefaultHandler {

    List myrelation;
    List myheader;
    List mytask;

    //to maintain context
    private Header tempheader;
    private Relation temprelation;
    private Task temptask;
    StringBuffer sb = new StringBuffer();

    public SAXParserExample() {
        myrelation = new ArrayList();
        mytask = new ArrayList();
        myheader = new ArrayList();
    }

    public void runExample() {
        this.parseDocument();
        this.printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("src/exercises-wise13.xml", this);
            sp.parse("src/alchemy-task.xml", this);
            sp.parse("src/exam-wise13.xml", this);
            sp.parse("src/exercises-wise11.xml", this);
            //sp.parse("src/tasks.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print the contents
     */
    private void printData() {
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String serverName = "localhost";
        String databaseName = "sql-alchemist-teamprojekt";
        
        //Database credentials
        String user = "root";
        String pass = "123";
        
        DBConnection dbConnection = new DBConnection(jdbcDriver, serverName, databaseName);
        
        
        System.out.println("No of Relations '" + myrelation.size() + "'.");

        Iterator it = myrelation.iterator();
        
        while (it.hasNext()) {
            Connection conn = dbConnection.buildDBConnection(user, pass);
            String sql = it.next().toString();
            dbConnection.executeSQLStatement(sql, conn);
            System.out.println(sql);
            dbConnection.closeDBConnection(conn);
        }
        
         System.out.println("No of Tasks '" + mytask.size() + "'.");

        it = mytask.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
         System.out.println("No of Header '" + myheader.size() + "'.");

        it = myheader.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
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
            temptask = new Task();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {

        sb.append(ch, start, length);
    }

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
            mytask.add(temptask);
        } else if (qName.equalsIgnoreCase("tasktexts")) {
            temptask.setTasktexts(sb.toString());
        } else if (qName.equalsIgnoreCase("referencestatement")) {
            temptask.setReferencestatement(sb.toString());
        } else if (qName.equalsIgnoreCase("evaluationstrategy")) {
            temptask.setEvaluationstrategy(sb.toString());
        } else if (qName.equalsIgnoreCase("term")) {
            temptask.setTerm(sb.toString());
        } else if (qName.equalsIgnoreCase("points")) {
            temptask.setPoints(Integer.parseInt(sb.toString()));
        }
        
        if (qName.equalsIgnoreCase("task")) {
            //add it to the list
            myheader.add(tempheader);
        } else if (qName.equalsIgnoreCase("title")) {
            tempheader.setTitle(sb.toString());
        } else if (qName.equalsIgnoreCase("flufftext")) {
            tempheader.setFluffytext(sb.toString());
        }

    }
}