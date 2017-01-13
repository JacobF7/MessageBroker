package broker.services.client;

import core.id.Id;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jacobfalzon on 11/01/2017.
 */
public class ClientService {

    private final ConcurrentHashMap<SocketChannel, Id> clients = new ConcurrentHashMap<>();

    /**
     * This is package private to ensure that only {@link ClientActivityService} can add Clients
     *
     * @param socketChannel
     * @param id
     */
    void addClient(final SocketChannel socketChannel, final Id id) {
        synchronized (clients) {
            clients.put(socketChannel, id);
        }
    }

    public Id getClientId(final SocketChannel socketChannel) {
        synchronized (clients) {
            return clients.get(socketChannel);
        }
    }

}
