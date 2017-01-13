package subscriber.event;

import core.event.EventHandler;
import core.id.Id;
import core.message.MessageStatus;
import core.utilities.LoggingUtils;
import core.message.Message;
import core.message.MessageType;
import core.utilities.TransferUtils;

import java.nio.channels.SocketChannel;

/**
 * Created by jacobfalzon on 01/01/2017.
 */
public class SubscriberEventHandler implements EventHandler {

    private Id subscriberId;

    @Override
    public void consume(final SocketChannel socketChannel, final Message consumeMessage) {

        LoggingUtils.info("RECEIVED -> [" + consumeMessage + "]");

        switch (consumeMessage.getMessageType()) {

            case CONNACK :
            case SUBACK :
            case PINGRESP :
            case UNSUBACK :
                break;

            case PUBLISH :
                produce(socketChannel, new Message(MessageType.PUBREC, subscriberId, consumeMessage.getTopic().get(), MessageStatus.SUCCESS.getPayload()));
                break;

            default:
                throw new UnsupportedOperationException("Type: [" + consumeMessage.getMessageType()  + "] cannot be consumed by Subscriber");
        }
    }

    @Override
    public void produce(final SocketChannel socketChannel, final Message produceMessage)  {

        LoggingUtils.info("SENT -> [" + produceMessage + "]");

        switch (produceMessage.getMessageType()) {

            case CONNECT :
                this.subscriberId = produceMessage.getId();
            case SUBSCRIBE:
            case PINGREQ :
            case PUBREC :
            case UNSUBSCRIBE:
                TransferUtils.write(socketChannel, produceMessage);
                break;

            case DISCONNECT:
                TransferUtils.write(socketChannel, produceMessage);
                TransferUtils.close(socketChannel);
                break;

            default:
                throw new UnsupportedOperationException("Type: [" + produceMessage.getMessageType() + "] cannot be produced by Subscriber");
        }
    }

}
