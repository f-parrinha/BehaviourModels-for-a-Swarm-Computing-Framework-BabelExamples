package bitVote.serializers;

import bitVote.common.Globals;
import bitVote.messages.VoteRequest;
import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static jatyc.lib.Utils.nonNull;

public class VoteRequestSerializer<T> implements ISerializer<T> {
    private static final Logger logger = nonNull(LogManager.getLogger(VoteRequestSerializer.class));

    @Override
    public void serialize(T message, ByteBuf byteBuf) throws IOException {
        if (!(message instanceof VoteRequest)) {
            logger.warn(Globals.WRONG_CLASS_FOR_SERIALIZER_MSG);
            return;
        }

        var voteRequest = (VoteRequest) message;
        byte[] roundIDBytes = voteRequest.getRoundID().getBytes(nonNull(StandardCharsets.UTF_8));
        byteBuf.writeInt(roundIDBytes.length);
        byteBuf.writeBytes(roundIDBytes);
    }

    @Override
    public T deserialize(ByteBuf byteBuf) {
        int roundIDLength = byteBuf.readInt();
        byte[] roundIDBytes = new byte[roundIDLength];
        byteBuf.readBytes(roundIDBytes);
        return (T) new VoteRequest(new String(roundIDBytes, nonNull(StandardCharsets.UTF_8)));
    }
}
