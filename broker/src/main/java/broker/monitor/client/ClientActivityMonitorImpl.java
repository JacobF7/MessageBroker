package broker.monitor.client;

import broker.monitor.manager.Monitor;
import broker.services.client.ClientActivityService;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Created by jacobfalzon on 10/01/2017.
 */
public class ClientActivityMonitorImpl implements ClientActivityMonitor, Monitor {

    private final ClientActivityService clientActivityService;

    public ClientActivityMonitorImpl(final ClientActivityService clientActivityService) {
        this.clientActivityService = clientActivityService;
    }

    @Override
    public Integer getConnectionCount() {
        return clientActivityService.getConnectionCount();
    }

    @Override
    public Integer getTimeout() {
        return clientActivityService.getTimeoutSeconds();
    }

    @Override
    public void setTimeout(final Integer timeoutSeconds) {
        clientActivityService.setTimeoutSeconds(timeoutSeconds);
    }

    @Override
    public ObjectName getObjectName() throws MalformedObjectNameException {
        return new ObjectName("broker.monitor.client:type=Runtime");
    }
}
