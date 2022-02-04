package com.pankov.roadtosenior.ioccontainer.exception;

public class NoUniqueBeanException extends RuntimeException {
    public NoUniqueBeanException(String message) {
        super(message);
    }

    public NoUniqueBeanException(String message, Throwable cause) {
        super(message, cause);
    }
}
