package core.message;

/**
 * An enumeration of all the possible message types.
 *
 * Created by jacobfalzon on 30/12/2016.
 */
public enum MessageType {
    CONNECT,
    CONNACK,
    SUBSCRIBE,
    SUBACK,
    PINGREQ,
    PINGRESP,
    PUBACK,
    PUBREC,
    PUBLISH,
    UNSUBSCRIBE,
    UNSUBACK,
    DISCONNECT
}
