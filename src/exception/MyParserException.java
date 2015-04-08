/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

/**
 *
 * @author Philips
 */
public class MyParserException implements ErrorHandler {
    
    public MyParserException() {}
    
    public void warning(SAXParseException e) throws SAXException {
        System.out.println("Achtung! Warnung: ");
        System.out.println(e.getMessage());
    }

    public void error(SAXParseException e) throws SAXException {
        System.out.println("Achtung! Fehler: ");
        System.out.println(e.getMessage());
    }

    public void fatalError(SAXParseException e) throws SAXException {
        System.out.println("Achtung! Fataler Fehler: ");
        System.out.println(e.getMessage());
    }
    
}
