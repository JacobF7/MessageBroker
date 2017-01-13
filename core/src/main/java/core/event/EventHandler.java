package core.event;

import core.message.Message;

import java.nio.channels.SocketChannel;

/**
 * Created by jacobfalzon on 03/01/2017.
 */
public interface EventHandler {

    void consume(SocketChannel socketChannel, Message consumeMessage);

    void produce(SocketChannel socketChannel, Message produceMessage);
}
