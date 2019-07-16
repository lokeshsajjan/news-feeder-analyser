package com.company.feeder;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import com.company.model.NewsFramer;


/**
 * @author lsajjan
 *
 */
class NewsBroker implements Consumer<ByteBuffer> {

    private static final NewsFramer framer = new NewsFramer();

    private final BlockingQueue<byte[]> queue;

    NewsBroker(int queueSize) {
        this.queue = new LinkedBlockingQueue<>(queueSize);
    }

    @Override
    public void accept(ByteBuffer buf) {
        byte[] bytes;
        int len = 0;
        do {
            bytes = this.queue.peek();
            if (bytes != null) {
                len = framer.toWire(buf, bytes);
                if (len > 0) {
                    this.queue.remove();
                }
            }
        } while (bytes != null && len > 0);
    }

    void put(byte[] bytes) throws InterruptedException {
        this.queue.put(bytes);
    }
}
