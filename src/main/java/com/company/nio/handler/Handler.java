package com.company.nio.handler;

/**
 * @author lsajjan
 *
 * @param <S>
 * @param <X>
 */
public interface Handler<S, X extends Throwable> {
    void handle(S s) throws X;
}
