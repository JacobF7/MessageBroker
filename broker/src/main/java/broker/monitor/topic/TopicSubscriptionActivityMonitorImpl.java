package broker.monitor.topic;

import broker.monitor.manager.Monitor;
import broker.services.message.MessagingService;
import broker.services.topic.TopicSubscriptionService;
import core.utilities.LoggingUtils;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jacobfalzon on 10/01/2017.
 */
public class TopicSubscriptionActivityMonitorImpl implements TopicSubscriptionActivityMonitor, Monitor {

    private final TopicSubscriptionService topicSubscriptionService;
    private final MessagingService messagingService;

    public TopicSubscriptionActivityMonitorImpl(final TopicSubscriptionService topicSubscriptionService, final MessagingService messagingService) {
        this.topicSubscriptionService = topicSubscriptionService;
        this.messagingService = messagingService;
    }

    @Override
    public Set<String> getSubscribedTopics() {
        return topicSubscriptionService.getTopics();
    }

    @Override
    public Map<String, Long> getDeliveredMessageCountPerTopic() {
        return messagingService.getDeliveredMessageCountPerTopic();
    }

    @Override
    public ObjectName getObjectName() throws MalformedObjectNameException {
        return new ObjectName("broker.monitor.topic:type=Runtime");
    }

}
