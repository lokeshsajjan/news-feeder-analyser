package com.company.nio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;


/**
 * @author lsajjan
 *
 */
public interface NioTcpProtocol extends Closeable {

    /**
     * A TCP protocol can be started optionally
     */
    void start();

    void handleRead(SelectionKey selectionKey) throws IOException;

    void handleWrite(SelectionKey selectionKey) throws IOException;

    interface Server extends NioTcpProtocol {
        void handleAccept(SelectionKey selectionKey) throws IOException;

        /**
         * Validates the given NIO context.
         *
         * @param ctx
         * @return true if valid, false if not. Invalid context leads to closing socket channel and cancelling selection key
         * @throws IOException
         */
        boolean validateContext(NioContext ctx) throws IOException;
    }

    interface Client extends NioTcpProtocol {
        void handleConnect(SelectionKey selectionKey) throws IOException;
    }
}
