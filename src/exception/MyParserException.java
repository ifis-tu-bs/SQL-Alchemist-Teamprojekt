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
        int zeile = e.getLineNumber();
        int spalte = e.getColumnNumber();
        System.out.println("Achtung! Warnung: Überprüfe Zeile " + zeile + ", Spalte " + spalte);
        System.out.println(e.getMessage());
    }

    public void error(SAXParseException e) throws SAXException {
        int zeile = e.getLineNumber();
        int spalte = e.getColumnNumber();
        System.out.println("Achtung! Fehler in Zeile " + zeile + ", Spalte " + spalte);
        System.out.println(e.getMessage());
    }

    public void fatalError(SAXParseException e) throws SAXException {
        int zeile = e.getLineNumber();
        int spalte = e.getColumnNumber();
        System.out.println("Achtung! Fataler Fehler in Zeile " + zeile + ", Spalte " + spalte);
        System.out.println(e.getMessage());
    }
    
}
