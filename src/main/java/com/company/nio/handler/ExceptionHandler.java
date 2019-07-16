package com.company.nio.handler;

import java.util.function.BiConsumer;

import com.company.utility.Util;


/**
 * @author lsajjan
 *
 * @param <S>
 * @param <X>
 */
public class ExceptionHandler<S, X extends Throwable> extends DecoratingHandler<S, X> {

    private final BiConsumer<S, Throwable> exceptionConsumer;

    public ExceptionHandler(Handler<S, X> handler, BiConsumer<S, Throwable> exceptionConsumer) {
        super(handler);
        this.exceptionConsumer = exceptionConsumer;
    }

    public ExceptionHandler(Handler<S, X> handler) {
        this(handler, (s, x) -> {
            Util.error("%s: %s", s, x);
            x.printStackTrace();
        });
    }

    @Override
    public void handle(S s) {
        try {
            super.handle(s);
        } catch (Throwable x) {
            this.exceptionConsumer.accept(s, x);
        }
    }
}
