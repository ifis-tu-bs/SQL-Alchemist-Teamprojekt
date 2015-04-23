package exception;

/**
 * Class MySQLAlchemistException.
 *
 * Handles Errors and Exceptions that could arrise during the execution of
 * SQL-Statements or during the parsing of the xml-file
 *
 * @author Tobias
 */
public class MySQLAlchemistException extends Exception {

    Exception exception;

    /**
     * Constructor for the exception
     *
     * @param message detailed message from the error
     */
    public MySQLAlchemistException(String message) {

        super();
    }
}
