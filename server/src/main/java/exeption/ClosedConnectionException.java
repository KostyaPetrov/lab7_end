package exeption;

/**
 * thrown when connection is closed
 */
public class ClosedConnectionException extends ConnectionExeption {
    public ClosedConnectionException() {
        super("server channel closed");
    }
}
