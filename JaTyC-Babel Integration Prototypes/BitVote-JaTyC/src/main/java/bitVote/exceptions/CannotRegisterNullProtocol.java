package bitVote.exceptions;

public class CannotRegisterNullProtocol extends RuntimeException {
    public CannotRegisterNullProtocol() {
        super("Cannot register NULL protocol");
    }
}

