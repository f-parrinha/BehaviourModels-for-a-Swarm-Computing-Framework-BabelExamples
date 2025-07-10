package abp.messages;

public class BitSend extends BitMessage {
    public static final short ID = 102;

    public BitSend(int bit) {
        super(ID, bit);
    }

    public BitSend() {
        super(ID, BitMessage.STARING_SENDING_BIT);
    }

    public void flip() {
        bit = bit == 0 ? 1 : 0;
    }
}
