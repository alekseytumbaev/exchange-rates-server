package com.example.exchangeratesserver.client.exception;

public class ResponseParsingException extends RuntimeException {
    public ResponseParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
