package com.example.exchangeratesserver.exception;

public class RatesAlreadyExistForDateException extends RuntimeException {
    public RatesAlreadyExistForDateException(String message) {
        super(message);
    }
}
