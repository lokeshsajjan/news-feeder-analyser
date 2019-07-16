package com.company.feeder;

import java.util.function.Supplier;

import com.company.model.News;
import com.company.model.NewsCoder;
import com.company.utility.Util;


/**
 * @author lsajjan
 *
 */
class NewsProducer implements Runnable {

    private final static NewsCoder coder = new NewsCoder();

    private final NewsBroker newsBroker;

    private final Supplier<News> supplier;

    NewsProducer(NewsBroker newsBroker, Supplier<News> supplier) {
        this.newsBroker = newsBroker;
        this.supplier = supplier;
    }

    @Override
    public void run() {
        final News news = this.supplier.get();
        Util.debug("produced %s", news);
        final byte[] encodedNews = coder.encode(news);
        try {
            // blocking if queue is full
            this.newsBroker.put(encodedNews);
        } catch (InterruptedException e) {
            Util.warn("producing news interrupted");
            Thread.currentThread().interrupt();
        }
    }
}
