package com.example.exchangeratesserver.exception.handler;

import lombok.Value;

@Value
public class BindingError {
    String name;
    String message;
}
