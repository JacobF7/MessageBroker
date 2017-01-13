package broker.event;

import broker.monitor.manager.MonitorManager;
import broker.services.client.ClientActivityService;
import broker.services.client.ClientService;
import broker.services.message.MessagingService;
import broker.monitor.message.MessageActivityMonitorImpl;
import broker.services.topic.TopicSubscriptionService;
import core.event.EventHandler;
import core.message.Message;
import core.message.MessageStatus;
import core.message.MessageType;
import core.utilities.LoggingUtils;
import core.utilities.TransferUtils;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.nio.channels.SocketChannel;

/**
 * Created by jacobfalzon on 03/01/2017.
 */
public class ServerEventHandler implements EventHandler {

    private final ClientService clientService = new ClientService();
    private final TopicSubscriptionService topicSubscriptionService = new TopicSubscriptionService(clientService);
    private final ClientActivityService clientActivityService = new ClientActivityService(clientService, topicSubscriptionService);
    private final MessagingService messagingService = new MessagingService();

    public ServerEventHandler() {
        MonitorManager.start(clientActivityService, messagingService, topicSubscriptionService);
    }

    @Override
    public void consume(final SocketChannel socketChannel, final Message consumeMessage) {

        LoggingUtils.info("RECEIVED -> [" + consumeMessage + "]");

        switch (consumeMessage.getMessageType()) {

            case CONNECT :
                clientActivityService.connect(socketChannel, consumeMessage.getId());
                produce(socketChannel, new Message(MessageType.CONNACK, consumeMessage.getId(), MessageStatus.SUCCESS.getPayload()));
                break;

            case SUBSCRIBE :
                clientActivityService.update(socketChannel);
                final MessageStatus subscribeStatus = topicSubscriptionService.subscribe(consumeMessage.getTopic().get(), socketChannel);
                produce(socketChannel, new Message(MessageType.SUBACK, consumeMessage.getId(), subscribeStatus.getPayload()));
                break;

            case PINGREQ :
                clientActivityService.update(socketChannel);
                produce(socketChannel, new Message(MessageType.PINGRESP, consumeMessage.getId(), MessageStatus.SUCCESS.getPayload()));
                break;

            case PUBLISH :
                clientActivityService.update(socketChannel);
                messagingService.addPublishedMessage(consumeMessage.getId());

                // Send Publish Acknowledgment
                produce(socketChannel, new Message(MessageType.PUBACK, consumeMessage.getId(), MessageStatus.SUCCESS.getPayload()));

                // Broadcast Message to all clients subscribed to the topic
                topicSubscriptionService.getMatchingSubscribers(consumeMessage.getTopic().get()).forEach(channel -> produce(channel, consumeMessage));
                break;

            case PUBREC :
                clientActivityService.update(socketChannel);
                break;

            case UNSUBSCRIBE :
                clientActivityService.update(socketChannel);
                final MessageStatus unsubscribeStatus = topicSubscriptionService.unsubscribe(consumeMessage.getTopic().get(), socketChannel);

                produce(socketChannel, new Message(MessageType.UNSUBACK, consumeMessage.getId(), consumeMessage.getTopic().get(), unsubscribeStatus.getPayload()));
                break;

            case DISCONNECT :
                // If the channel was a subscriber remove all of its subscriptions!
                topicSubscriptionService.unsubscribe(socketChannel);
                clientActivityService.discard(socketChannel);
                break;

            default:
                throw new UnsupportedOperationException("Type: [" + consumeMessage.getMessageType()  + "] cannot be consumed by Server");
        }

        messagingService.addMessageCount();
    }

    @Override
    public void produce(final SocketChannel socketChannel, final Message produceMessage) {

        LoggingUtils.info("SENT -> [" + produceMessage + "]");

        switch (produceMessage.getMessageType()) {

            case PUBLISH :
                messagingService.addDeliveredMessageByTopic(produceMessage.getTopic().get());
            case CONNACK :
            case SUBACK :
            case PINGRESP :
            case PUBACK :
            case UNSUBACK :
                messagingService.addDeliveredMessageByClient(clientService.getClientId(socketChannel));
                TransferUtils.write(socketChannel, produceMessage);
                break;

            default:
                throw new UnsupportedOperationException("Type: [" + produceMessage.getMessageType()  + "] cannot be consumed by Server");
        }

        messagingService.addMessageCount();
    }
}
