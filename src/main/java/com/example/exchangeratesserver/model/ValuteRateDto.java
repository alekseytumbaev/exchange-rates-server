package com.example.exchangeratesserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValuteRateDto {
    private int digitCode;
    private String chCode;
    private String valuteName;
    private int quantity;
    private BigDecimal rate;
    private LocalDate date;
}
