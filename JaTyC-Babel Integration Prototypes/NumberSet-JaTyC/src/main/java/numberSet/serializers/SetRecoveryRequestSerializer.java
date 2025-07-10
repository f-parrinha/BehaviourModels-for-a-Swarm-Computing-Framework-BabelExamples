package numberSet.serializers;

import io.netty.buffer.ByteBuf;
import numberSet.common.Globals;
import numberSet.messages.SetRecoveryRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;

import static jatyc.lib.Utils.nonNull;

public class SetRecoveryRequestSerializer <T> implements ISerializer<T> {
    private static final Logger logger = nonNull(LogManager.getLogger(SetRecoveryRequestSerializer.class));

    @Override
    public void serialize(T t, ByteBuf byteBuf) throws IOException {
        if (!(t instanceof SetRecoveryRequest)) {
            logger.warn(Globals.WRONG_CLASS_FOR_SERIALIZER_MSG);
            return;
        }
    }

    @Override
    public T deserialize(ByteBuf byteBuf) throws IOException {
        return (T) new SetRecoveryRequest();
    }
}
