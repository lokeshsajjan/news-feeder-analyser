package com.company.analyser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

import com.company.nio.NioContext;
import com.company.nio.handler.Handler;


/**
 * @author lsajjan
 *
 */
public class ReadHandler implements Handler<SelectionKey, IOException> {

    private final Consumer<ByteBuffer> consumer;

    public ReadHandler(Consumer<ByteBuffer> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void handle(SelectionKey key) throws IOException {
        final SocketChannel sc = (SocketChannel) key.channel();
        final NioContext nioContext = (NioContext) key.attachment();

        final ByteBuffer buf = nioContext.getReadBuffer();
        int read = sc.read(buf);
        if (read == -1) { // channel end
            nioContext.endReadStream();
            return;
        }

        if (read > 0) {
            this.consumer.accept(buf);
        }
        // for news analyser always interested in reading news
    }
}
