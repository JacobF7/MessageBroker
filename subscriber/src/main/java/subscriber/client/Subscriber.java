package subscriber.client;

import client.Client;
import subscriber.event.SubscriberEventHandler;
import subscriber.message.SubscriberMessageGenerator;

import java.io.IOException;

/**
 * Created by jacobfalzon on 02/01/2017.
 */
public class Subscriber extends Client {

    public Subscriber() throws IOException {
        super(new SubscriberEventHandler(), new SubscriberMessageGenerator());
    }

    public static void main(final String[] args) throws IOException {
        final Subscriber subscriber = new Subscriber();
    }
}
