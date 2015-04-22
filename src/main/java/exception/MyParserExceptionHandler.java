package exception;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class MyParserExceptionHandler implements ErrorHandler {
    
    private static final Logger logger = LogManager.getLogger(MyParserExceptionHandler.class.getName());
    /**
     * Constructor MyParserException.
     */
    public MyParserExceptionHandler() {
        //Nothing to do.
    }
    
    /**
     * Method warning.
     * Print the warning with the line number, if the xml-file is not correct
     * 
     * @param e SAXParseException
     * @throws SAXException 
     */
    @Override
    public void warning(SAXParseException e) throws SAXException {
        int zeile = e.getLineNumber();
        int spalte = e.getColumnNumber();
        logger.warn("Achtung! Warnung: Überprüfe Zeile " + zeile + ", Spalte " + spalte);
        logger.warn(e.getMessage());
    }
    
    /**
     * Method error.
     * Print the error with the line number, if the xml-file is not correct
     * 
     * @param e SAXParseException
     * @throws SAXException 
     */
    @Override
    public void error(SAXParseException e) throws SAXException {
        int zeile = e.getLineNumber();
        int spalte = e.getColumnNumber();
        logger.error("Achtung! Fehler in Zeile " + zeile + ", Spalte " + spalte);
        logger.error(e.getMessage());
    }
    
    /**
     * Method fatalError.
     * Print the fatal error with the line number, if the xml-file is not correct
     * 
     * @param e SAXParseException
     * @throws SAXException 
     */
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        int zeile = e.getLineNumber();
        int spalte = e.getColumnNumber();
        logger.error("Achtung! Fataler Fehler in Zeile " + zeile + ", Spalte " + spalte);
        logger.error(e.getMessage());
    }
}
