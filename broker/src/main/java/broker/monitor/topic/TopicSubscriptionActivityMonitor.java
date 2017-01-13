package broker.monitor.topic;

import javax.management.MXBean;
import java.util.Map;
import java.util.Set;

/**
 * Created by jacobfalzon on 10/01/2017.
 */
@MXBean
public interface TopicSubscriptionActivityMonitor {

    Set<String> getSubscribedTopics();

    Map<String, Long> getDeliveredMessageCountPerTopic();
}
