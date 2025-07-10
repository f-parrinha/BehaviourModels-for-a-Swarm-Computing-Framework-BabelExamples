package bitVote.handlers.messages;

import bitVote.BitVoteProtocol;
import bitVote.messages.VoteAck;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.MessageInHandler;
import pt.unl.fct.di.novasys.network.data.Host;

public class VoteAckHandler<T extends ProtoMessage> implements MessageInHandler<T> {
    private final BitVoteProtocol protocol;

    public VoteAckHandler(BitVoteProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void receive(T t, Host host, short protoId, int channelId) {
        if (t instanceof VoteAck) {
            protocol.uponVoteAck((VoteAck) t, host, protoId, channelId);
        }
    }
}
