package publisher.client;

import client.Client;
import publisher.event.PublisherEventHandler;
import publisher.message.PublisherMessageGenerator;

import java.io.IOException;

/**
 * Created by jacobfalzon on 11/12/2016.
 */
public class Publisher extends Client {

    public Publisher() throws IOException {
        super(new PublisherEventHandler(), new PublisherMessageGenerator());
    }

    public static void main(final String[] args) throws IOException {
        final Publisher publisher = new Publisher();
    }
}
