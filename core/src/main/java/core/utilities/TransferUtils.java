package core.utilities;

import core.message.Message;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Optional;

/**
 * Created by jacobfalzon on 03/01/2017.
 */
public class TransferUtils {

    public static void write(final SocketChannel socketChannel, final Message message) {
        final byte[] serializedMessage = SerializationUtils.serialize(message);
        final ByteBuffer buffer = ByteBuffer.allocate(serializedMessage.length);
        buffer.put(serializedMessage);
        buffer.flip();

        while (buffer.hasRemaining()) {
            try {
                socketChannel.write(buffer);
            } catch (final IOException e) {
                throw new IllegalStateException("Failed to write!");
            }
        }
    }

    public static Optional<Message> read(final SocketChannel socketChannel) {

        final ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
            socketChannel.read(buffer);
            return Optional.of(SerializationUtils.deserialize(buffer.array()));
        } catch (final IOException | SerializationException e) {
            close(socketChannel);
            return Optional.empty();
        }
    }

    public static void close(final SocketChannel socketChannel) {
        try {
            socketChannel.close();
        } catch (final IOException e) {
            LoggingUtils.error("Failed to close Client");
        }
    }

}
