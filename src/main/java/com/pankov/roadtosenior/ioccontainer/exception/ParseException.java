package com.pankov.roadtosenior.ioccontainer.exception;

import javax.xml.stream.XMLStreamException;

public class ParseException extends RuntimeException {

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
