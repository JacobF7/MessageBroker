package broker.services.topic;

import broker.services.client.ClientService;
import core.message.MessageStatus;
import core.topic.Topic;
import core.utilities.LoggingUtils;
import core.utilities.StringUtils;

import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jacobfalzon on 07/01/2017.
 */
public class TopicSubscriptionService {

    private static final String LOGGING_TYPE = StringUtils.tab() + "TOPIC SUBSCRIPTION SERVICE";
    private final ConcurrentHashMap<Topic, HashSet<SocketChannel>> subscribedTopics = new ConcurrentHashMap<>();
    private final ClientService clientService;

    public TopicSubscriptionService(final ClientService clientService) {
        this.clientService = clientService;
    }

    public MessageStatus subscribe(final Topic topic, final SocketChannel socketChannel) {

        final MessageStatus messageStatus;

        if (subscribedTopics.get(topic) == null) {
            // If no Client is subscribed to this topic, create a new list with a single client
            subscribedTopics.put(topic, new HashSet<>(Collections.singleton(socketChannel)));
            messageStatus = MessageStatus.SUCCESS;

        } else if (!subscribedTopics.get(topic).contains(socketChannel)) {
            // If there are Clients subscribed to this topic, add the Client to the list (if not present)
            subscribedTopics.get(topic).add(socketChannel);
            messageStatus = MessageStatus.SUCCESS;

        } else {
            // The Client must already be subscribed to this topic
            messageStatus = MessageStatus.SUBSCRIBE_ERROR;
        }

        LoggingUtils.log(LOGGING_TYPE,  "Subscribed Clients : " + printSubscribers());
        return messageStatus;
    }

    public MessageStatus unsubscribe(final Topic topic, final SocketChannel socketChannel) {

        final HashSet<SocketChannel> subscribers = getSubscribers(topic);

        if(subscribers == null || !subscribers.contains(socketChannel)) {
            // If no Client is subscribed to the topic, we can stop
            LoggingUtils.log(LOGGING_TYPE, "The Client is not subscribed to the given [" + topic + "]");
            return MessageStatus.UNSUBSCRIBE_ERROR;
        }

        // At this point the Client is subscribed to the specified topic
        if (subscribers.size() == 1) {
            // If a single client exists for this topic, then the entry is removed from the map
            subscribedTopics.remove(topic);
        } else {
            // If more than one client exists for this topic, then the client is removed from the value set
            subscribers.remove(socketChannel);
        }

        LoggingUtils.log(LOGGING_TYPE, "Clients after unsubscribe from [" + topic + "] : " + printSubscribers());
        return MessageStatus.SUCCESS;
    }

    public void unsubscribe(final SocketChannel socketChannel) {

        synchronized (subscribedTopics) {
            subscribedTopics.keySet()
                            .stream()
                            .filter(topic -> subscribedTopics.get(topic).contains(socketChannel))
                            .forEach(topic -> this.unsubscribe(topic, socketChannel));
        }
    }

    public Set<SocketChannel> getMatchingSubscribers(final Topic topic) {
        return getSubscribedTopics().keySet()
                                    .stream()
                                    .filter(topic::matches)
                                    .flatMap(someTopic -> getSubscribers(someTopic).stream())
                                    .collect(Collectors.toSet());
    }

    public Set<String> getTopics() {
        return subscribedTopics.keySet()
                .stream()
                .map(Topic::toString)
                .collect(Collectors.toSet());
    }

    private Map<Topic, HashSet<SocketChannel>> getSubscribedTopics() {
        return subscribedTopics;
    }

    private HashSet<SocketChannel> getSubscribers(final Topic topic) {
        return subscribedTopics.get(topic);
    }

    private String printSubscribers() {

        final String prefix = StringUtils.newline() + StringUtils.tab() + StringUtils.tab();

        final Function<Map.Entry<Topic, HashSet<SocketChannel>>, String> toString =
                entry -> prefix + entry.getKey() + " -> " + printSubscribers(entry.getValue());

        return subscribedTopics.entrySet()
                               .stream()
                               .map(toString)
                               .reduce(String::concat)
                               .orElse("N/A");
    }

    private String printSubscribers(final HashSet<SocketChannel> socketChannels) {
        return socketChannels.stream().map(clientService::getClientId).collect(Collectors.toSet()).toString();
    }

}
