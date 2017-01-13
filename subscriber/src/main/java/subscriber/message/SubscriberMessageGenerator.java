package subscriber.message;

import core.id.Id;
import core.message.Message;
import core.message.MessageGenerator;
import core.message.MessageType;
import core.topic.TopicGeneratorImpl;
import core.topic.TopicGenerator;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jacobfalzon on 05/01/2017.
 */
public class SubscriberMessageGenerator implements MessageGenerator {

    private static final SecureRandom GENERATOR = new SecureRandom();
    private static final TopicGenerator TOPIC_GENERATOR = new TopicGeneratorImpl();

    @Override
    public Message generate(final Id id) {

        final List<MessageType> messages = generates();
        final int randomIndex = GENERATOR.nextInt(messages.size());

        switch (messages.get(randomIndex)) {

            case PINGREQ :
                return new Message(MessageType.PINGREQ, id);

            case SUBSCRIBE :
                return new Message(MessageType.SUBSCRIBE, id, TOPIC_GENERATOR.generate());

            case UNSUBSCRIBE :
                return new Message(MessageType.UNSUBSCRIBE, id, TOPIC_GENERATOR.generate());

            case DISCONNECT:
                final double randomNumber = Math.round(Math.random());

                if(randomNumber == 0) { // To avoid often disconnections, we toss a coin before
                    return new Message(MessageType.DISCONNECT, id);
                }

                return generate(id);

            default:
                throw new UnsupportedOperationException("Unsupported Message Type Generated [" + messages.get(randomIndex) + "]");
        }
    }

    @Override
    public List<MessageType> generates() {
        return Arrays.asList(MessageType.PINGREQ, MessageType.SUBSCRIBE, MessageType.UNSUBSCRIBE, MessageType.DISCONNECT);
    }

    @Override
    public int getDelay() {
        return 7;
    }

}
