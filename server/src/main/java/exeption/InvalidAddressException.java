package exeption;
/**
 * thrown when address is invalid
 */
public class InvalidAddressException extends ConnectionExeption {
    public InvalidAddressException() {
        super("invalid address");
    }

    public InvalidAddressException(String s) {
        super(s);
    }
}