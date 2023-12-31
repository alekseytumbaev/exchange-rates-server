package com.example.exchangeratesserver.controller;

import com.example.exchangeratesserver.model.ValuteRateDto;
import com.example.exchangeratesserver.service.ValuteRateService;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/rates")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ValuteRateController {

    private final ValuteRateService rateService;

    @GetMapping
    public Iterable<ValuteRateDto> findByCurDate(@RequestParam @PastOrPresent LocalDate date) {
        Iterable<ValuteRateDto> rates = rateService.findByCurDate(date);
        log.info("Отправлены курсы за дату {}", date);
        return rates;
    }

    @GetMapping("/dynamic")
    public Iterable<ValuteRateDto> getCursDynamic(@RequestParam @Past LocalDate from,
                                                  @RequestParam @PastOrPresent LocalDate to,
                                                  @RequestParam String code) {
        Iterable<ValuteRateDto> dynamics = rateService.getCursDynamic(from, to, code);
        log.info("Отправлена динамика курсов. Дата начала - '{}', дата конца - '{}', код валюты - '{}'", from, to, code);
        return dynamics;
    }
}
