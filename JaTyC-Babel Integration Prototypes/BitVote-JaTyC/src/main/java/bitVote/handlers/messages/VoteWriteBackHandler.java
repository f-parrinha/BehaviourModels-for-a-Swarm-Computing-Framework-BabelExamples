package bitVote.handlers.messages;

import bitVote.BitVoteProtocol;
import bitVote.messages.VoteWriteBack;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.MessageInHandler;
import pt.unl.fct.di.novasys.network.data.Host;

public class VoteWriteBackHandler<T extends ProtoMessage> implements MessageInHandler<T> {
    private final BitVoteProtocol protocol;

    public VoteWriteBackHandler(BitVoteProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void receive(T t, Host host, short protoId, int channelId) {
        if (t instanceof VoteWriteBack) {
            protocol.uponVoteWriteBack((VoteWriteBack) t, host, protoId, channelId);
        }
    }
}
