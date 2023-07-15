package com.example.exchangeratesserver.exception;

public class ValuteCodeNotSupportedException extends RuntimeException {
    public ValuteCodeNotSupportedException(String message) {
        super(message);
    }
}
