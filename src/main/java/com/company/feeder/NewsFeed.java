package com.company.feeder;

import static com.company.utility.Util.getIntConfig;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.company.nio.NioContext;
import com.company.nio.NioTcpProtocol;
import com.company.nio.handler.ConnectionHandler;
import com.company.nio.handler.ExceptionHandler;
import com.company.nio.handler.Handler;
import com.company.utility.Util;


/**
 * @author lsajjan
 *
 */
class NewsFeed implements NioTcpProtocol.Client {

    private final int newsProducingRateInMillis;

    private final NewsBroker newsBroker;

    private final NewsProducer newsProducer;

    private final Handler<SelectionKey, IOException> connectionHandler;

    private final Handler<SelectionKey, IOException> writeHandler;

    private ScheduledExecutorService scheduler;

    NewsFeed(int newsProducingRateInMillis) {
        this.newsBroker = new NewsBroker(getIntConfig("newsQueueSize", 32));
        this.newsProducer = new NewsProducer(this.newsBroker, new NewsSupplier());
        this.newsProducingRateInMillis = newsProducingRateInMillis;
        this.connectionHandler = new ConnectionHandler(
                () -> NioContext.onlyWrite(Util.DEFAULT_BUF_LENGTH),
                addr -> start(addr.toString()));
        this.writeHandler = new ExceptionHandler<>(new WriteHandler(this.newsBroker),
                (k, t) -> {
                    throw new IllegalStateException(t); // leads to closing client
                });
    }

    void start(String feedAddr) {
        // if news creation is slow and producing rate is high, must tune the config of scheduler
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "f" + feedAddr));
        this.scheduler.scheduleAtFixedRate(this.newsProducer, this.newsProducingRateInMillis,
                this.newsProducingRateInMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() throws IOException {
        Util.shutdownAndAwaitTermination(this.scheduler, "news-feed", 5);
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleRead(SelectionKey selectionKey) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleWrite(SelectionKey selectionKey) throws IOException {
        this.writeHandler.handle(selectionKey);
    }

    @Override
    public void handleConnect(SelectionKey selectionKey) throws IOException {
        this.connectionHandler.handle(selectionKey);
    }
}
