package bitVote.serializers;

import bitVote.common.Globals;
import bitVote.messages.VoteAck;
import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static jatyc.lib.Utils.nonNull;

public class VoteAckSerializer<T> implements ISerializer<T> {
    private static final Logger logger = nonNull(LogManager.getLogger(VoteAckSerializer.class));

    @Override
    public void serialize(T message, ByteBuf byteBuf) throws IOException {
        if (!(message instanceof VoteAck)) {
            logger.warn(Globals.WRONG_CLASS_FOR_SERIALIZER_MSG);
            return;
        }

        var voteAck = (VoteAck) message;
        byte[] roundIDBytes = voteAck.getRoundID().getBytes(nonNull(StandardCharsets.UTF_8));
        byteBuf.writeInt(roundIDBytes.length);
        byteBuf.writeBytes(roundIDBytes);
        byteBuf.writeInt(voteAck.getBit());
    }

    @Override
    public T deserialize(ByteBuf byteBuf) {
        int roundIDLength = byteBuf.readInt();
        byte[] roundIDBytes = new byte[roundIDLength];
        byteBuf.readBytes(roundIDBytes);
        int bit = byteBuf.readInt();
        return (T) new VoteAck(new String(roundIDBytes, nonNull(StandardCharsets.UTF_8)), bit);
    }
}
