package client;

import core.event.EventHandler;
import core.id.Id;
import core.message.Message;
import core.message.MessageGenerator;
import core.message.MessageType;
import core.message.ScheduledMessageQueueService;
import core.utilities.LoggingUtils;
import core.utilities.TransferUtils;
import broker.server.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by jacobfalzon on 02/01/2017.
 */
public class Client {

    private final EventHandler eventHandler;
    private final Selector selector;
    private final Id id;
    private final ScheduledMessageQueueService scheduledMessageQueueService;

    public Client(final EventHandler eventHandler, final MessageGenerator messageGenerator) throws IOException {
        this.eventHandler = eventHandler;
        this.selector = Selector.open();
        this.id = Id.generate();
        this.scheduledMessageQueueService = new ScheduledMessageQueueService(messageGenerator, id);

        configure(Server.HOSTNAME, Server.PORT_NUMBER);
        select();
    }

    private void configure(final String hostname, final int portNumber) throws IOException {
        final SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(hostname, portNumber));
    }

    public void select() throws IOException {

        boolean online = true;

        while(online) {

            if (getSelector().select() > 0) {

                final Iterator<SelectionKey> keyIterator = getSelector().selectedKeys().iterator();

                while (keyIterator.hasNext()) {

                    final SelectionKey selectionKey = keyIterator.next();
                    keyIterator.remove();

                    connect(selectionKey); // Connect

                    read(selectionKey); // Read

                    write(selectionKey); // Write

                    if(!selectionKey.isValid()) {
                        LoggingUtils.info("Closing Client " + getId());
                        scheduledMessageQueueService.shutdown();
                        online = false;
                        break;
                    }
                }
            }
        }
    }

    public void connect(final SelectionKey selectionKey) throws IOException {

        if (selectionKey.isValid() && selectionKey.isConnectable()) {

            // Connected to the Server
            LoggingUtils.info("Client is connected to Server");
            final SocketChannel clientChannel = (SocketChannel) selectionKey.channel();

            while(clientChannel.isConnectionPending()) {
                clientChannel.finishConnect();
            }

            // Produce a Connection Message
            getEventHandler().produce(clientChannel, new Message(MessageType.CONNECT, getId()));

            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    public void read(final SelectionKey selectionKey) throws IOException {

        if (selectionKey.isValid() && selectionKey.isReadable()) {

            final SocketChannel clientChannel = (SocketChannel) selectionKey.channel();

            TransferUtils.read(clientChannel).ifPresent(message -> {

                getEventHandler().consume(clientChannel, message);

                // The selection key is interested in writing
                selectionKey.interestOps(SelectionKey.OP_WRITE);
            });

        }
    }

    public void write(final SelectionKey selectionKey) {

        if (selectionKey.isValid() && selectionKey.isWritable()) {

            final SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
            selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            scheduledMessageQueueService.serve().ifPresent(generatedMessage -> getEventHandler().produce(clientChannel, generatedMessage));
        }
    }

    public Selector getSelector() {
        return selector;
    }

    public Id getId() {
        return id;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }
}