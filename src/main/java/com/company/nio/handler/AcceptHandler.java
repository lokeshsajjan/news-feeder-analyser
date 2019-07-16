package com.company.nio.handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Supplier;

import com.company.nio.NioContext;
import com.company.utility.Util;

/**
 * Handling the Tcp Accept events
 * @author lsajjan
 *
 */
public class AcceptHandler implements Handler<SelectionKey, IOException> {

    private final Supplier<NioContext> supplier;

    public AcceptHandler(Supplier<NioContext> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void handle(SelectionKey key) throws IOException {
        final ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        final SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        Util.debug("<AcceptHandler> from %s", sc.getRemoteAddress());

        final NioContext nioContext = this.supplier.get();
        final SelectionKey clientKey = sc.register(key.selector(), nioContext.getInterestOps());
        clientKey.attach(nioContext);
    }
}
