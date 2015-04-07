package xmlparse;

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

public class SAXParserExample extends DefaultHandler{

	List myrelation;
        List myheader;
        List mytask;
	
	private String tempVal;
	
	//to maintain context
	private Header tempheader;
        private Relation temprelation;
        private Task temptask;
	StringBuffer sb = new StringBuffer();
	
	public SAXParserExample(){
		myrelation = new ArrayList();
	}
	
	public void runExample() {
		parseDocument();
		printData();
	}

	private void parseDocument() {
		
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
		
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			
			//parse the file and also register this class for call backs
			sp.parse("src/exercises-wise13.xml", this);
			
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * Iterate through the list and print
	 * the contents
	 */
	private void printData(){
		
		System.out.println("No of Relations '" + myrelation.size() + "'.");
		
		Iterator it = myrelation.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}
	

	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//reset
		sb.setLength(0);
		if(qName.equalsIgnoreCase("Relation")) {
			//create a new instance of employee
			temprelation = new Relation();
			//temprelation.setType(attributes.getValue("type"));
		}
	}
	

	public void characters(char[] ch, int start, int length) throws SAXException {
            
            sb.append(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equalsIgnoreCase("relation")) {
			//add it to the list
			myrelation.add(temprelation);
			
		}else if (qName.equalsIgnoreCase("intension")) {
			temprelation.setIntension(sb.toString());
		}else if (qName.equalsIgnoreCase("tuple")) {
			temprelation.setTuple(sb.toString());
		}
		
	}
}
