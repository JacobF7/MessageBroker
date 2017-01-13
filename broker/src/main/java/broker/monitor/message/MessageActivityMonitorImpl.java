package broker.monitor.message;

import broker.monitor.manager.Monitor;
import broker.services.message.MessagingService;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Map;

/**
 * Created by jacobfalzon on 09/01/2017.
 */
public class MessageActivityMonitorImpl implements MessageActivityMonitor, Monitor {

    private final MessagingService messagingService;

    public MessageActivityMonitorImpl(final MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @Override
    public Long getMessageCount() {
        return messagingService.getMessageCount();
    }

    @Override
    public Map<String, Long> getDeliveredMessageCountPerClient() {
        return messagingService.getDeliveredMessageCountPerClient();
    }

    @Override
    public Map<String, Long> getPublishedMessageCount() {
        return messagingService.getPublishedMessageCount();
    }

    @Override
    public ObjectName getObjectName() throws MalformedObjectNameException {
        return new ObjectName("broker.monitor.message:type=Runtime");
    }
}
