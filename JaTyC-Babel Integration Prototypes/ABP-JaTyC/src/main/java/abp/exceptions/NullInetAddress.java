package abp.exceptions;

import abp.common.Globals;

public class NullInetAddress extends RuntimeException {
    public NullInetAddress() {
        super(Globals.NULL_INET_ADDRESS_MSG);
    }
}
