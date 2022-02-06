package com.pankov.roadtosenior.ioccontainer.exception;

public class PostBeanConfigurationException extends RuntimeException {
    public PostBeanConfigurationException(String message) {
        super(message);
    }

    public PostBeanConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
