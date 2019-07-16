package com.company.nio.handler;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.company.nio.NioContext;
import com.company.utility.Util;


/**
 * @author lsajjan
 *
 */
public class ConnectionHandler implements Handler<SelectionKey, IOException> {

    private final Supplier<NioContext> supplier;

    private final Consumer<SocketAddress> consumer;

    public ConnectionHandler(Supplier<NioContext> supplier, Consumer<SocketAddress> consumer) {
        this.supplier = supplier;
        this.consumer = consumer;
    }

    @Override
    public void handle(SelectionKey key) throws IOException {
        final SocketChannel sc = (SocketChannel) key.channel();
        if (sc.finishConnect()) {
            Util.info("<ConnectionHandler> connected to %s", sc.getRemoteAddress());
            final NioContext nioContext = this.supplier.get();
            key.interestOps(nioContext.getInterestOps());
            key.attach(nioContext);
            this.consumer.accept(sc.getLocalAddress());
        }
    }
}
