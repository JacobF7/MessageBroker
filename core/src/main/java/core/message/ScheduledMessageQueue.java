package core.message;

import core.id.Id;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Created by jacobfalzon on 06/01/2017.
 */
public class ScheduledMessageQueue implements Runnable {

    private final Queue<Message> messages;
    private final MessageGenerator messageGenerator;
    private final Id id;

    public ScheduledMessageQueue(final MessageGenerator messageGenerator, final Id id) {
        this.messages = new ConcurrentLinkedQueue<>();
        this.messageGenerator = messageGenerator;
        this.id = id;
    }

    @Override
    public void run() {
        getMessages().add(getMessageGenerator().generate(getId()));
    }

    public Optional<Message> serve() {
        return Optional.ofNullable(getMessages().poll());
    }

    public Queue<Message> getMessages() {
        return messages;
    }

    public MessageGenerator getMessageGenerator() {
        return messageGenerator;
    }

    public Id getId() {
        return id;
    }
}
