package com.example.exchangeratesserver.client.exception;

public class IllegalStartEndDate extends RuntimeException {
    public IllegalStartEndDate(String message) {
        super(message);
    }
}
