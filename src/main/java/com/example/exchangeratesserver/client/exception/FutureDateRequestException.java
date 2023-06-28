package com.example.exchangeratesserver.client.exception;

public class FutureDateRequestException extends RuntimeException {
    public FutureDateRequestException(String message) {
        super(message);
    }
}
