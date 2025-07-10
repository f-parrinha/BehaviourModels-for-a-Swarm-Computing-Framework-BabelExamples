package abp.exceptions;

public class NoPropertiesFound extends RuntimeException {
    public NoPropertiesFound() {
        super("No Properties instance was found");
    }
}
