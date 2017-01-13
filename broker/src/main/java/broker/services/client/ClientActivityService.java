package broker.services.client;

import broker.services.topic.TopicSubscriptionService;
import core.id.Id;
import core.utilities.LoggingUtils;
import core.utilities.StringUtils;
import core.utilities.TransferUtils;

import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

/**
 * Created by jacobfalzon on 08/01/2017.
 */
public class ClientActivityService implements Runnable {

    private static final String LOGGING_TYPE = StringUtils.tab() + "CLIENT ACTIVITY SERVICE";

    private volatile Integer timeoutSeconds = 10; // Default (Configurable through JMC)
    private static final int AUDIT_RATE = 2;

    private final ConcurrentHashMap<SocketChannel, Instant> clientActivityMonitor = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService;
    private final ClientService clientService;
    private final TopicSubscriptionService topicSubscriptionService;

    public ClientActivityService(final ClientService clientService, final TopicSubscriptionService topicSubscriptionService) {
        this.clientService = clientService;
        this.topicSubscriptionService = topicSubscriptionService;

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(this,
                AUDIT_RATE,
                AUDIT_RATE,
                TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        synchronized (clientActivityMonitor) {
            clientActivityMonitor.keySet()
                    .stream()
                    .filter(this::isInactive)
                    .forEach(this::deactivate);
       }
    }

    public void connect(final SocketChannel socketChannel, final Id id) {

        final Instant currentTime = Instant.now();

        synchronized (clientActivityMonitor) {
            clientActivityMonitor.put(socketChannel, currentTime);
            clientService.addClient(socketChannel, id);

            LoggingUtils.log(LOGGING_TYPE, "Client " + id + " connected at " + currentTime);
        }
    }

    public void update(final SocketChannel socketChannel) {

        final Instant currentTime = Instant.now();

        synchronized (clientActivityMonitor) {
            clientActivityMonitor.replace(socketChannel, currentTime);

            LoggingUtils.log(LOGGING_TYPE,"Client " + clientService.getClientId(socketChannel) + " activity updated to " + currentTime);
        }
    }

    public Integer getConnectionCount() {
       synchronized (clientActivityMonitor) {
            return clientActivityMonitor.keySet().size();
       }
    }

    public void discard(final SocketChannel socketChannel) {
       synchronized (clientActivityMonitor) {
            clientActivityMonitor.remove(socketChannel);
            TransferUtils.close(socketChannel);
       }
    }

    private void deactivate(final SocketChannel socketChannel) {
        LoggingUtils.log(LOGGING_TYPE,"Client " + clientService.getClientId(socketChannel) + " has been disconnected since time-out of " + getTimeoutSeconds() + " seconds has expired (Current Time : " + Instant.now() + ")");
        discard(socketChannel);

        // This is required in case the client was subscribed to some topic
        topicSubscriptionService.unsubscribe(socketChannel);
    }


    private boolean isInactive(final SocketChannel socketChannel) {
        return Duration.between(clientActivityMonitor.get(socketChannel), Instant.now()).toMillis() >=  TimeUnit.SECONDS.toMillis(getTimeoutSeconds());
    }

    public void shutdown() {
        getExecutorService().shutdownNow();
    }

    public synchronized void setTimeoutSeconds(final Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    private ScheduledExecutorService getExecutorService() {
        return executorService;
    }
}
