package abp.messages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;

public abstract class BitMessage extends ProtoMessage {
    public static final int STARING_SENDING_BIT = 0;
    public static final int STARTING_RECEIVER_BIT = 1;
    private static final Logger logger = LogManager.getLogger(BitMessage.class);

    protected int bit;

    public BitMessage(short id, int bit) {
        super(id);

        this.bit = bit;
        validateBit();
    }

    public int getBit() {
        validateBit();
        return bit;
    }

    @Override
    public String toString() {
        return "bit: " + bit;
    }

    // Checks whether the value is really one or zero
    protected void validateBit() {
        if (!(bit == 1 || bit == 0)) {
            logger.error("Wrong bit value '{}'. It can only be either '1' or '0'", bit);
        }
    }
}
