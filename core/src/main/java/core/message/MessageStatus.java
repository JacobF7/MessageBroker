package core.message;

/**
 * Created by jacobfalzon on 12/01/2017.
 */
public enum MessageStatus {

    SUCCESS("Ok"),
    UNSUBSCRIBE_ERROR("Error: Client is not subscribed to topic"),
    SUBSCRIBE_ERROR("Error: Client is already subscribed to topic");

    private String representation;

    MessageStatus(final String representation) {
        this.representation = representation;
    }

    public String getPayload() {
        return representation;
    }
}
