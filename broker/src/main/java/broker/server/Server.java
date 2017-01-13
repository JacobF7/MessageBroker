package broker.server;

import broker.event.ServerEventHandler;
import core.utilities.LoggingUtils;
import core.utilities.TransferUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by jacobfalzon on 11/12/2016.
 */
public class Server {

    public static final String HOSTNAME = "localhost";
    public static final int PORT_NUMBER = 9999;

    private final Selector selector;
    private final ServerEventHandler serverEventHandler = new ServerEventHandler();

    public Server() throws IOException {
        this.selector = Selector.open();
        configure(HOSTNAME, PORT_NUMBER);
        select();
    }

    private void configure(final String hostname, final int portNumber) throws IOException {
        final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(hostname, portNumber));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, serverSocketChannel.validOps());
    }

    public void select() throws IOException {

        LoggingUtils.info("Server has started");

        while(true) {

            if (selector.select() > 0) {

                final Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {

                    final SelectionKey selectionKey = keyIterator.next();
                    keyIterator.remove();

                    accept(selectionKey); // Accept

                    read(selectionKey); // Read

                    if(!selectionKey.isValid()) {
                        // This can only occur when a Client Channel is closed, hence we cancel the Selection Key!
                        selectionKey.cancel();
                    }
                }
            }
        }

        // serverEventHandler.getClientActivityService().shutdown();
    }

    private void accept(final SelectionKey selectionKey) throws IOException {

        if (selectionKey.isValid() && selectionKey.isAcceptable()) {

            // Accept a new client connection
            final ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
            final SocketChannel clientChannel = serverChannel.accept();
            clientChannel.configureBlocking(false);

            // Server is interested for Read Events related to this Client Channel
            clientChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
        }
    }

    private void read(final SelectionKey selectionKey) {

        if (selectionKey.isValid() && selectionKey.isReadable()) {
            final SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
            TransferUtils.read(clientChannel).ifPresent(message -> serverEventHandler.consume(clientChannel, message));
        }
    }
}
