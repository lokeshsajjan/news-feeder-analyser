package com.company.feeder;

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
public class WriteHandler implements Handler<SelectionKey, IOException> {

    private final Consumer<ByteBuffer> consumer;

    public WriteHandler(Consumer<ByteBuffer> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void handle(SelectionKey key) throws IOException {
        final ByteBuffer buffer = ((NioContext) key.attachment()).getWriteBuffer();
        this.consumer.accept(buffer);

        buffer.flip();
        final SocketChannel sc = (SocketChannel) key.channel();
        if (buffer.hasRemaining()) {
            sc.write(buffer);
        }
        buffer.compact();
        // for news feed always interested in writing news
    }
}
