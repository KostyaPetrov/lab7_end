package exeption;
/**
 * thrown when port already in use
 */
public class PortAlreadyInUseException extends ConnectionExeption {
    public PortAlreadyInUseException() {
        super("port already in use");
    }
}
