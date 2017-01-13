package broker.services.message;

import core.id.Id;
import core.topic.Topic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jacobfalzon on 09/01/2017.
 */
public class MessagingService {

    private Long messageCount = 0L;
    private Map<String, Long> deliveredMessagesPerTopic = new HashMap<>();
    private Map<String, Long> deliveredMessagesPerClient = new HashMap<>();
    private Map<String, Long> publishedMessages = new HashMap<>();

    public Long getMessageCount() {
        return messageCount;
    }

    public Map<String, Long> getDeliveredMessageCountPerTopic() {
        return deliveredMessagesPerTopic;
    }

    public Map<String, Long> getDeliveredMessageCountPerClient() {
        return deliveredMessagesPerClient;
    }

    public Map<String, Long> getPublishedMessageCount() {
        return publishedMessages;
    }

    public void addMessageCount() {
        messageCount++;
    }

    public void addDeliveredMessageByTopic(final Topic topic) {
        add(deliveredMessagesPerTopic, topic.getTopic());
    }

    public void addDeliveredMessageByClient(final Id id) {
        add(deliveredMessagesPerClient, id.getId());
    }

    public void addPublishedMessage(final Id id) {
        add(publishedMessages, id.getId());
    }

    private <T> void add(final Map<T, Long> clientMessages, final T key) {

        if (clientMessages.get(key) == null) {
            clientMessages.put(key, 1L);
        } else {
            final Long count = clientMessages.get(key);
            clientMessages.put(key, count+1L);
        }
    }
}
