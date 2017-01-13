package core.message;

import core.id.Id;
import core.topic.Topic;

import java.io.Serializable;
import java.util.Optional;

/**
 * This class serves as a holder for messages that are transferred to/from Client and Server.
 * A message contains a {@link MessageType}, the {@link Id} of the sender, an optional {@link String}
 * holding the payload and an optional {@link Topic}.
 *
 * Created by jacobfalzon on 30/12/2016.
 */
public class Message implements Serializable {

    private final MessageType messageType;
    private final Id id;
    private final String payload;
    private final Topic topic;

    public Message(final MessageType messageType, final Id id) {
        this(messageType, id, null, null);
    }

    public Message(final MessageType messageType, final Id id, final Topic topic) {
        this(messageType, id, topic, null);
    }

    public Message(final MessageType messageType, final Id id, final String payload) {
        this(messageType, id, null, payload);
    }

    public Message(final MessageType messageType, final Id id, final Topic topic, final String payload) {
        this.messageType = messageType;
        this.id = id;
        this.topic = topic;
        this.payload = payload;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Id getId() {
        return id;
    }

    public Optional<String> getPayload() {
        return Optional.ofNullable(payload);
    }

    public Optional<Topic> getTopic() { return Optional.ofNullable(topic);}

    @Override
    public String toString() {
        return "Message { " + id
                        + ", Type = " + messageType
                        + getTopic().map(topic ->  ", " + topic.toString()).orElse("")
                        + getPayload().map(payload -> ", Payload = " + payload + "}")
                                      .orElse("}");
    }
}
