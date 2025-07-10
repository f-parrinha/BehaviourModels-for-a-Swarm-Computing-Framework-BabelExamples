package numberSet.exceptions;

public class NoBabelInstanceFound extends RuntimeException {
    private static final String message = "No Babel instance found";

    public NoBabelInstanceFound() {
        super("No Babel instance found");
    }
}
