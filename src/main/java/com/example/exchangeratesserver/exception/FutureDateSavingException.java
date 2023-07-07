package com.example.exchangeratesserver.exception;

public class FutureDateSavingException extends RuntimeException {
    public FutureDateSavingException(String message) {
        super(message);
    }
}
