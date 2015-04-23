/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlparse;

import exception.MyParserExceptionHandler;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class XMLSyntaxCheck.
 * 
 * Check the syntax of a xml-file.
 * 
 * @author Philips
 */
public class XMLSyntaxCheck {

    private static final Logger logger = LogManager.getLogger(XMLSyntaxCheck.class.getName());
    
    /** 
     * checks if the given parameter s (e.g. tasks.xml) is a valid XML-file
     * regarding XML-schema tasks.xsd
     * @param s name of the xml-file you want to validate (like "tasks.xml")
     */
    public void checkxml(String s) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);

            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

            factory.setSchema(schemaFactory.newSchema(
            new Source[] {new StreamSource("input/xml/tasks.xsd")})); // source of xml-schema-file

            SAXParser parser = factory.newSAXParser();

            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(new MyParserExceptionHandler());
            reader.parse(new InputSource("input/xml/" + s));  //source of xml-file
            System.out.println("Datei ist valide."); // if this line is executed, the file is valid.
        } catch (SAXException | ParserConfigurationException | IOException e) {
            StringBuffer sb = new StringBuffer();
            sb.append(e.toString());
            StringTokenizer st = new StringTokenizer(sb.toString(), "\n");
            logger.error(st.nextToken());
        }
    }
}
