package com.company.nio;

import java.nio.ByteBuffer;

/**
 * Encodes and decodes a message using some schema.
 * @author lsajjan
 *
 * @param <T>
 */
public interface MessageCoder<T> {

    /**
     * Encodes the given message using some schema into a byte array.
     *
     * @param msg
     * @return
     */
    byte[] encode(T msg);

    /**
     * Decodes a message from the given byte buffer.
     *
     * @param buf a byte buffer guaranteed to hold a whole message encoded according to the same schema used to
     *            encode the message.
     * @return
     */
    T decode(ByteBuffer buf);
}
