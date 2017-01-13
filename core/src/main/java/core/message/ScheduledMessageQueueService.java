package core.message;

import core.id.Id;

import java.util.Optional;
import java.util.concurrent.*;

/**
 * Created by jacobfalzon on 07/01/2017.
 */
public class ScheduledMessageQueueService {

    private static final int INITIAL_DELAY = 5;

    private final ScheduledMessageQueue scheduledMessageQueue;
    private final ScheduledExecutorService executorService;

    public ScheduledMessageQueueService(final MessageGenerator messageGenerator, final Id id) {
        this.scheduledMessageQueue = new ScheduledMessageQueue(messageGenerator, id);

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(scheduledMessageQueue,
                                               INITIAL_DELAY,
                                               messageGenerator.getDelay(),
                                               TimeUnit.SECONDS);
    }

    public Optional<Message> serve() {
        return scheduledMessageQueue.serve();
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}


