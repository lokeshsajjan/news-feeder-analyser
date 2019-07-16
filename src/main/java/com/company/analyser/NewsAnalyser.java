package com.company.analyser;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.company.nio.NioContext;
import com.company.nio.NioTcpProtocol;
import com.company.nio.handler.AcceptHandler;
import com.company.nio.handler.ExceptionHandler;
import com.company.nio.handler.Handler;
import com.company.utility.Util;


/**
 * @author lsajjan
 *
 */
public class NewsAnalyser implements NioTcpProtocol.Server {

    private final AtomicInteger analyserThreadCount = new AtomicInteger(0);

    private final Handler<SelectionKey, IOException> acceptHandler;

    private final NewsBroker newsBroker;

    private final Handler<SelectionKey, IOException> readHandler;

    private final int numberOfWorkers;

    private final NewsAnalysingInspector inspector;

    private ExecutorService executorService;

    public NewsAnalyser(int numberOfWorkers) {
        this.acceptHandler = new ExceptionHandler<>(new AcceptHandler(
                () -> NioContext.onlyRead(Util.DEFAULT_BUF_LENGTH)));
        this.newsBroker = new NewsBroker();
        this.readHandler = new ExceptionHandler<>(new ReadHandler(new NewsAssembler(this.newsBroker)));
        this.numberOfWorkers = numberOfWorkers;
        this.inspector = new NewsAnalysingInspector(3);
    }

    @Override
    public void start() {
        this.executorService = Executors.newFixedThreadPool(this.numberOfWorkers,
                r -> new Thread(r, "analyser-" + this.analyserThreadCount.incrementAndGet()));
        for (int i = 0; i < this.numberOfWorkers; i++) {
            this.executorService.execute(new NewsAnalysingWorker(this.newsBroker, this.inspector));
        }
        this.inspector.start();
    }

    @Override
    public void handleAccept(SelectionKey selectionKey) throws IOException {
        this.acceptHandler.handle(selectionKey);
    }

    @Override
    public void handleRead(SelectionKey selectionKey) throws IOException {
        this.readHandler.handle(selectionKey);
    }

    @Override
    public void handleWrite(SelectionKey selectionKey) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean validateContext(NioContext ctx) throws IOException {
        return !ctx.isReadStreamEnded();
    }

    @Override
    public void close() throws IOException {
        Util.shutdownAndAwaitTermination(this.executorService, "news-analyser", 5);
        this.inspector.close();
    }
}
