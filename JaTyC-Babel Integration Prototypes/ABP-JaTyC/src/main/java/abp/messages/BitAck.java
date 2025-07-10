package abp.messages;

public class BitAck extends BitMessage {
    public static final short ID = 101;

    public BitAck(int bit) {
        super(ID, bit);
    }
}