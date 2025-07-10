package bitVote.exceptions;


import bitVote.common.Globals;

public class NullInetAddress extends RuntimeException {
    public NullInetAddress() {
        super(Globals.NULL_INET_ADDRESS_MSG);
    }
}
