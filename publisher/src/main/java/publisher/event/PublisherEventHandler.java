package publisher.event;

import core.event.EventHandler;
import core.utilities.LoggingUtils;
import core.message.Message;
import core.utilities.TransferUtils;

import java.nio.channels.SocketChannel;

/**
 * Created by jacobfalzon on 03/01/2017.
 */
public class PublisherEventHandler implements EventHandler {

    @Override
    public void consume(final SocketChannel socketChannel, final Message consumeMessage) {

        LoggingUtils.info("RECEIVED -> [" + consumeMessage + "]");

        switch (consumeMessage.getMessageType()) {

            case CONNACK :
            case PINGRESP :
            case PUBACK :
                break;

            default:
                throw new UnsupportedOperationException("Type: [" + consumeMessage.getMessageType()  + "] cannot be consumed by Publisher");

        }
    }

    @Override
    public void produce(final SocketChannel socketChannel, final Message produceMessage) {

        LoggingUtils.info("SENT -> [" + produceMessage + "]");

        switch (produceMessage.getMessageType()) {

            case CONNECT :
            case PINGREQ :
            case PUBLISH :
                TransferUtils.write(socketChannel, produceMessage);
                break;

            case DISCONNECT:
                TransferUtils.write(socketChannel, produceMessage);
                TransferUtils.close(socketChannel);
                break;

            default:
                throw new UnsupportedOperationException("Type: [" + produceMessage.getMessageType() + "] cannot be produced by Publisher");
        }
    }

}
