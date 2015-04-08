/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlparse;
package exception;

import exception.MyParserException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author Philips
 */
public class XMLSyntaxCheck {

    /**
     * @param args the command line arguments
     */
    
    /** 
     * checks if the given parameter s (e.g. tasks.xml) is a valid XML-file
     * regarding XML-schema tasks.xsd
     * @param s name of the xml-file you want to validate (like "tasks.xml")
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException 
     */
    
    public void checkxml(String s) throws SAXException, ParserConfigurationException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        SchemaFactory schemaFactory = 
        SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        factory.setSchema(schemaFactory.newSchema(
        new Source[] {new StreamSource("src/tasks.xsd")})); // source of xml-schema-file

        SAXParser parser = factory.newSAXParser();

        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(new MyParserException());
        reader.parse(new InputSource("src/" + s));  //source of xml-file
        System.out.println("Alles tippitoppi!"); // if this line is executed, the file is valid.
    }
    
}
