package com.pankov.roadtosenior.ioccontainer;

import com.pankov.roadtosenior.ioccontainer.exception.BeanInstantiationException;

import java.util.function.Consumer;

public interface ThrowingConsumer <T> extends Consumer<T> {

    @Override
    default void accept(final T method) {
        try {
            acceptThrows(method);
        } catch (ReflectiveOperationException exception) {
            throw new BeanInstantiationException("Error during invoke ", exception);
        }
    }

    void acceptThrows(T method) throws ReflectiveOperationException;
}
