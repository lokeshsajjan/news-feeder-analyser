package com.company.analyser;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;

import com.company.model.News;
import com.company.model.NewsCoder;
import com.company.model.NewsFramer;

/**
 * @author lsajjan
 *
 */
class NewsAssembler implements Consumer<ByteBuffer> {

    private static final NewsFramer framer = new NewsFramer();

    private static final NewsCoder coder = new NewsCoder();

    private final Consumer<News> consumer;

    NewsAssembler(Consumer<News> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void accept(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        Optional<ByteBuffer> data;
        do {
            data = framer.fromWire(byteBuffer);
            data.map(coder::decode).ifPresent(this.consumer);
        } while (data.isPresent());
        byteBuffer.compact();
    }
}
