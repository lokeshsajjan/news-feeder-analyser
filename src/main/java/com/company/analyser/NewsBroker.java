package com.company.analyser;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.company.model.News;
import com.company.utility.Util;


/**
 * @author lsajjan
 *
 */
class NewsBroker implements Consumer<News> {

    private final BlockingQueue<News> queue;

    NewsBroker() {
        this.queue = new LinkedBlockingQueue<>(1024);
    }

    @Override
    public void accept(News news) {
        if (!this.queue.offer(news)) {
            Util.warn("<NewsBroker> discard %s", news);
        }
    }

    News poll(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return this.queue.poll(timeout, timeUnit);
    }
}
