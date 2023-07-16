package com.example.exchangeratesserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@SpringBootApplication
@ServletComponentScan
public class ExchangeRatesServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeRatesServerApplication.class, args);
    }

}
