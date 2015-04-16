package exception;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

/**
 * Class MyParserException.
 * 
 * Handles Errors and Exceptions that could arrise during the XML-syntaxcheck
 * 
 * @author Philip HolzhÃ¼ter
 */

// @todo Bennennt den Krams mal gescheit!
// @todo Warum ist denn Eure Exception gar keine Exception sondern n Error Hanlder??
// @todo also, ihr Fangt in diesem ErrorHanlder SAX fehler ein, und gebt die in Form von sinnigen Exceptions wieder raus.
public class MyParserException implements ErrorHandler {
    
    /**
     * Constructor MyParserException.
     */
    public MyParserException() {
        //Nothing to do.
    }
    
    /**
     * Method warning.
     * 
     * @param e SAXParseException
     * @throws SAXException 
     */
    @Override
    public void warning(SAXParseException e) throws SAXException {
        int zeile = e.getLineNumber();
        int spalte = e.getColumnNumber();
        System.out.println("Achtung! Warnung: Überprüfe Zeile " + zeile + ", Spalte " + spalte);
        System.out.println(e.getMessage());
    }
    
    /**
     * Method error.
     * 
     * @param e SAXParseException
     * @throws SAXException 
     */
    @Override
    public void error(SAXParseException e) throws SAXException {
        int zeile = e.getLineNumber();
        int spalte = e.getColumnNumber();
        System.out.println("Achtung! Fehler in Zeile " + zeile + ", Spalte " + spalte);
        System.out.println(e.getMessage());
    }
    
    /**
     * Method fatalError.
     * 
     * @param e SAXParseException
     * @throws SAXException 
     */
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        int zeile = e.getLineNumber();
        int spalte = e.getColumnNumber();
        System.out.println("Achtung! Fataler Fehler in Zeile " + zeile + ", Spalte " + spalte);
        System.out.println(e.getMessage());
    }
}
