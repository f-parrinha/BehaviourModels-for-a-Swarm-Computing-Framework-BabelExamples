package bitVote.utills;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class SerializeUtils {

    /**
     * Writes dada in the buffer in order to serialize a string
     * @param byteBuf Netty ByteBuf
     */
    public static void serializeString(ByteBuf byteBuf, String string) {
        byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(stringBytes.length);
        byteBuf.writeBytes(stringBytes);
    }

    /**
     * Reads dada in the buffer in order to deserialize a string
     * <p> PRE: Must be called in the correct order of deserialization in the buffer</p>
     * @param byteBuf Netty ByteBuf
     * @return String
     */
    public static String deserializeString(ByteBuf byteBuf) {
        int stringBytesLength = byteBuf.readInt();
        byte[] stringBytes = new byte[stringBytesLength];
        byteBuf.readBytes(stringBytes);
        return new String(stringBytes, StandardCharsets.UTF_8);
    }
}
