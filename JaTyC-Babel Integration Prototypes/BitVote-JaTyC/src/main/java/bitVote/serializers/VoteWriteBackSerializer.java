package bitVote.serializers;

import bitVote.common.Globals;
import bitVote.common.utils.SerializeUtils;
import bitVote.messages.VoteWriteBack;
import bitVote.voting.VoteRound;
import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;

import static jatyc.lib.Utils.nonNull;

public class VoteWriteBackSerializer<T> implements ISerializer<T> {
    private static final Logger logger = nonNull(LogManager.getLogger(VoteWriteBackSerializer.class));

    @Override
    public void serialize(T message, ByteBuf byteBuf) throws IOException {
        if (!(message instanceof VoteWriteBack)) {
            logger.warn(Globals.WRONG_CLASS_FOR_SERIALIZER_MSG);
            return;
        }

        var voteWriteBack = (VoteWriteBack) message;
        SerializeUtils.serializeString(byteBuf, voteWriteBack.getRoundID());
        byteBuf.writeInt(voteWriteBack.getDecidedBit());
        SerializeUtils.serializeString(byteBuf, nonNull(voteWriteBack.getStatus().toString()));
    }

    @Override
    public T deserialize(ByteBuf byteBuf) {
        String roundID = SerializeUtils.deserializeString(byteBuf);
        int bit = byteBuf.readInt();
        var status = VoteRound.Status.valueOf(SerializeUtils.deserializeString(byteBuf));
        return (T) new VoteWriteBack(roundID, bit, status);
    }
}