package broker.monitor.manager;

import broker.monitor.client.ClientActivityMonitorImpl;
import broker.monitor.message.MessageActivityMonitorImpl;
import broker.monitor.topic.TopicSubscriptionActivityMonitorImpl;
import broker.services.client.ClientActivityService;
import broker.services.message.MessagingService;
import broker.services.topic.TopicSubscriptionService;
import core.utilities.LoggingUtils;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

/**
 * Created by jacobfalzon on 10/01/2017.
 */
public class MonitorManager {

    private static MBeanServer MANAGEMENT_CONTROL_SERVER = ManagementFactory.getPlatformMBeanServer();

    public static void start(final ClientActivityService clientActivityService,
                        final MessagingService messagingService,
                        final TopicSubscriptionService topicSubscriptionService) {

        register(new ClientActivityMonitorImpl(clientActivityService));
        register(new MessageActivityMonitorImpl(messagingService));
        register(new TopicSubscriptionActivityMonitorImpl(topicSubscriptionService, messagingService));
    }

    private static void register(final Monitor monitor) {
        try {
            MANAGEMENT_CONTROL_SERVER.registerMBean(monitor, monitor.getObjectName());
        } catch (final Exception e) {
            LoggingUtils.error("Failed to start Monitor");
        }
    }
}
