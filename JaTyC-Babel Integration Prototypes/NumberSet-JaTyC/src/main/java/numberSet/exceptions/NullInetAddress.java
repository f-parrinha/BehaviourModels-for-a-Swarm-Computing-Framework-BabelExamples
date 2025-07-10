package numberSet.exceptions;


import numberSet.common.Globals;

public class NullInetAddress extends RuntimeException {
    public NullInetAddress() {
        super(Globals.NULL_INET_ADDRESS_MSG);
    }
}
