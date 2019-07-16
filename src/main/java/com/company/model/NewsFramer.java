package com.company.model;

import java.nio.ByteBuffer;
import java.util.Optional;

import com.company.nio.MessageFramer;

/**
 * Use one byte to denote message length.
 * <p>
 * Optionally use compression
 * </p>
 * @author lsajjan
 *
 */
public class NewsFramer implements MessageFramer {

    @Override
    public int toWire(ByteBuffer buffer, byte[] msg) {
        if (buffer.remaining() < msg.length + 1) {
            return 0;
        }
        buffer.put((byte) msg.length).put(msg);
        return msg.length + 1;
    }

    @Override
    public Optional<ByteBuffer> fromWire(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return Optional.empty();
        }
        buffer.mark();
        final int len = buffer.get();
        if (buffer.remaining() < len) {
            buffer.reset();
            return Optional.empty();
        }

        final int limit = buffer.limit();
        buffer.limit(buffer.position() + len);
        try {
            return Optional.of(buffer.asReadOnlyBuffer());
        } finally {
            buffer.limit(limit);
            buffer.position(buffer.position() + len);
        }
    }
}
