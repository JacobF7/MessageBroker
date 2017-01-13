package publisher.message;

import core.id.Id;
import core.topic.TopicGeneratorImpl;
import core.topic.TopicGenerator;
import core.message.Message;
import core.message.MessageGenerator;
import core.message.MessageType;
import core.utilities.StringUtils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jacobfalzon on 04/01/2017.
 */
public class PublisherMessageGenerator implements MessageGenerator {

    private static final SecureRandom GENERATOR = new SecureRandom();
    private static final TopicGenerator TOPIC_GENERATOR = new TopicGeneratorImpl();

    @Override
    public Message generate(final Id id) {

        final List<MessageType> messages = generates();
        final int randomIndex = GENERATOR.nextInt(messages.size());

        switch (messages.get(randomIndex)) {

            case PINGREQ :
                return new Message(MessageType.PINGREQ, id);

            case PUBLISH :
                return new Message(MessageType.PUBLISH, id, TOPIC_GENERATOR.generate(), StringUtils.generate());

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
        return Arrays.asList(MessageType.PINGREQ, MessageType.PUBLISH, MessageType.DISCONNECT);
    }

    @Override
    public int getDelay() {
        return 8;
    }
}
