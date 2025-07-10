package numberSet.exceptions;

public class BadNumberSetUpdate extends Exception {
    public static final String message = "Something wrong happened during NumberSet update. (Hint: No unique number found?)";

    public BadNumberSetUpdate() {
        super(message);
    }
}
