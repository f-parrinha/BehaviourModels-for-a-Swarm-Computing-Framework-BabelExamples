package numberSet.exceptions;

public class NullClassException extends RuntimeException {
    public NullClassException() {
        super("Null class found in getClass call");
    }
}
