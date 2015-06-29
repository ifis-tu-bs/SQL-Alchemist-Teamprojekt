package de.tu_bs.cs.ifis.sqlgame.xmlparse;

import de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.typesafe.config.*;
import de.tu_bs.cs.ifis.sqlgame.dbconnection.DBConnection;
import de.tu_bs.cs.ifis.sqlgame.sandbox.Task;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.h2.tools.DeleteDbFiles;

/**
 * Class MySAXParser.
 *
 * Class to parse a xml and put the information into java-task-objects.
 *
 * @author Tobias Gruenhagen, Philip Holzhueter, Tobias Runge
 */
public class MySAXParser extends DefaultHandler {

    private ArrayList<Relation> myRelation;
    private ArrayList<Header> myHeader;
    private ArrayList<Exercise> myExercise;
    private ArrayList<Task> myTasks;

    private Header tempHeader;
    private Relation tempRelation;
    private Exercise tempExercise;

    private final StringBuffer sb;

    /**
     * Getter for myRelation.
     *
     * @return List, list with relationinformation
     */
    public ArrayList<Relation> getMyRelation() {
        return this.myRelation;
    }

    /**
     * Getter for myHeader
     *
     * @return List, list with headerinformation
     */
    public ArrayList<Header> getMyHeader() {
        return this.myHeader;
    }

    /**
     * Getter for myExercise.
     *
     * @return List, list with exerciseinformation
     */
    public ArrayList<Exercise> getMyExercise() {
        return this.myExercise;
    }

    /**
     * Getter for myTask.
     *
     * @return List, list with tasks
     */
    public ArrayList<Task> getMyTasks() {
        return this.myTasks;
    }

    /**
     * Setter for myTask.
     *
     * @param myTasks List, list with tasks
     */
    public void setMyTasks(ArrayList<Task> myTasks) {
        this.myTasks = myTasks;
    }

    /**
     * Constructor MySAXParser.
     *
     * Create empty lists and define default variables.
     */
    public MySAXParser() {
        this.myRelation = new ArrayList<>();
        this.myExercise = new ArrayList<>();
        this.myHeader = new ArrayList<>();
        this.myTasks = new ArrayList<>();
        this.sb = new StringBuffer();
    }

    /**
     * Method parseDocument.
     * 
     * Parse the XML-File and validate the syntax of the containung
     * SQL-statements
     * 
     * @param exercise the xml-file that is parsed
     * @throws de.tu_bs.cs.ifis.sqlgame.exception.MySQLAlchemistException
     *         exception from parsing the document
     */
    public void parseDocument(String exercise) throws MySQLAlchemistException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //New instance of a parser
            SAXParser sp = spf.newSAXParser();
            
            //Parse the file and register this class for call backs
            Config conf = ConfigFactory.load();
            sp.parse(conf.getString("input.xmlPath") + exercise, this);
            
            //Check SQL-syntax
            DBConnection dbconn = new DBConnection("memory", conf.getString("input.testDbPath"));
            Iterator it0 = this.myTasks.iterator();
            while (it0.hasNext()) {
                Task t = (Task) it0.next();
                
                //Check CREATE-TABLE and INSERT-INTO statements
                Iterator it1 = t.getMyRelation().iterator();
                
                    
                    
                    
                while (it1.hasNext()) {
                    Relation r = (Relation) it1.next();
                    dbconn.executeSQLUpdateStatement(conf.getString("auth.user"), conf.getString("auth.pass"), r.getIntension());
                    dbconn.executeSQLUpdateStatement(conf.getString("auth.user"), conf.getString("auth.pass"), r.getTuple());
                }

                //Check referencestatements
                Iterator it2 = t.getMyExercise().iterator();
                while (it2.hasNext()) {
                    Exercise e = (Exercise) it2.next();
                    dbconn.executeSQLSelectStatement(conf.getString("auth.user"), conf.getString("auth.pass"), e.getReferencestatement());
                }
            }
            
            //Delete the in-memory-database
            DeleteDbFiles.execute(conf.getString("input.dbsPath"), "test", true);
        } catch (SAXException | ParserConfigurationException | IOException se) {
            throw new MySQLAlchemistException("Fehler beim Parsen des Dokuments ", se);
        }
    }
    
    public ArrayList<ArrayList> getColumnInformation(Relation rel) {
        String createTableStatement = rel.getIntension();
        ArrayList<ArrayList> resultList = new ArrayList();
        ArrayList<String> foreignColumns = new ArrayList();

        String compPrimaryKey = "";

        String beforeStatement;

        String createTableStatementForForeigns = createTableStatement;

        if (createTableStatementForForeigns.contains("FOREIGN KEY")) {
            String[] stringarrayF = createTableStatementForForeigns.split(", FOREIGN KEY ");
            for (int ii = 1; ii < stringarrayF.length; ii++) {
                foreignColumns.add(stringarrayF[ii]);
            }
            String lastElement = foreignColumns.get(foreignColumns.size() - 1);
            foreignColumns.set(foreignColumns.size() - 1, lastElement.substring(0, lastElement.length() - 1));
            ArrayList<String> foreignColumnsClone = (ArrayList) foreignColumns.clone();
            foreignColumns.clear();
            for (String str : foreignColumnsClone) {
                String[] elements = str.split(" ");
                String element = elements[0];
                String ref = elements[2];

                foreignColumns.add(element);
                foreignColumns.add(ref);
            }

        }

        if (createTableStatement.contains("PRIMARY KEY(")) {
            String[] stringarray = createTableStatement.split(", PRIMARY KEY");
            beforeStatement = stringarray[0];
            compPrimaryKey = stringarray[1];
            StringTokenizer sto2 = new StringTokenizer(compPrimaryKey, ")");
            compPrimaryKey = sto2.nextToken();
            compPrimaryKey = compPrimaryKey.replace("(", "");
        } else {
            beforeStatement = createTableStatement;
        }
        StringTokenizer st = new StringTokenizer(beforeStatement, ",");
        ArrayList<String> columns = new ArrayList();
        String info = "";
        String columninfo = st.nextToken();
        StringTokenizer st2 = new StringTokenizer(columninfo, "(");
        st2.nextToken();
        info += (st2.nextToken());

        columns.add(info);

        while (st.hasMoreTokens()) {
            columns.add(st.nextToken());
        }

        for (int i = 0; i < columns.size(); i++) {
            String s = columns.get(i);
            if (s.contains(")") && !s.contains("(")) {
                s = s.replace(")", "");
            }
            columns.set(i, s);
        }
        int countOfColumns = columns.size();
        for (int i = 0; i < countOfColumns; i++) {

            ArrayList partsOfColumnInfo = new ArrayList();
            String cInfo = columns.get(i);
            StringTokenizer st3 = new StringTokenizer(cInfo, " ");
            while (st3.hasMoreTokens()) {
                partsOfColumnInfo.add(st3.nextToken());
            }
            String attributeName = (String) partsOfColumnInfo.get(0);
            String attributeType = (String) partsOfColumnInfo.get(1);
            boolean containsPrimary = partsOfColumnInfo.contains("primary") || partsOfColumnInfo.contains("PRIMARY");
            partsOfColumnInfo.clear();
            partsOfColumnInfo.add(attributeName);
            partsOfColumnInfo.add(attributeType);
            partsOfColumnInfo.add(containsPrimary);
            partsOfColumnInfo.add(null);

            resultList.add((ArrayList) partsOfColumnInfo);
        }

        String[] primaryColumns = compPrimaryKey.split(", ");
        for (ArrayList cols : resultList) {
            for (String s : primaryColumns) {
                if (cols.contains(s)) {
                    cols.set(2, true);
                }
            }
        }

        for (ArrayList cols : resultList) {
            for (int iii = 0; iii < foreignColumns.size() - 1; iii++) {
                String s = foreignColumns.get(iii);

                s = s.replace("(", "");
                s = s.replace(")", "");
                foreignColumns.set(iii, s);

                if (cols.contains(s)) {

                    cols.set(3, foreignColumns.get(iii + 1));
                }
                iii++;
            }
        }

        return resultList;
    }

    /**
     * Method startElement. Event Handler
     *
     * Helper-method to parse the document, take specific actions at the start
     * of each element.
     *
     * @param uri String, the Namespace URI, or the empty string if the element
     * has no Namespace URI or if Namespace processing is not being performed.
     * @param localName String, the local name (without prefix), or the empty
     * string if Namespace processing is not being performed.
     * @param qName String, the qualified name (with prefix), or the empty
     * string if qualified names are not available.
     * @param attributes The attributes attached to the element. If there are no
     * attributes, it shall be an empty Attributes object.
     * @throws SAXException exception from parsing the document
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
     * @throws SAXException exception from parsing the document
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
     * @throws SAXException exception from parsing the document
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
         else if (qName.equalsIgnoreCase("datageneration")) {
            this.tempRelation.setDataGeneration(this.sb.toString());
        }
        else if (qName.equalsIgnoreCase("primarykey")) {
            this.tempRelation.setPrimaryKey(this.sb.toString());
        }
        else if (qName.equalsIgnoreCase("tablename")) {
            this.tempRelation.setTableName(this.sb.toString());
        }

        if (qName.equalsIgnoreCase("subtask")) {
            //add it to the list
            this.myExercise.add(this.tempExercise);
        } else if (qName.equalsIgnoreCase("subtaskid")) {
            this.tempExercise.setSubTaskId(Integer.parseInt(this.sb.toString()));
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
