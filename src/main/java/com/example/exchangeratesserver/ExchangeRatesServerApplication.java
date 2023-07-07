package com.example.exchangeratesserver;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@RequiredArgsConstructor
public class ExchangeRatesServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeRatesServerApplication.class, args);
    }

}
