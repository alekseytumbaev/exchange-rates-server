package com.example.exchangeratesserver.controller;

import com.example.exchangeratesserver.model.ValuteRateDto;
import com.example.exchangeratesserver.service.ValuteRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.example.exchangeratesserver.client.model.ValuteCode.USD;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ValuteRateService rateService;

    @Value("${notifications.rates-updates.test-notification}")
    private boolean updateRatesTestNotifications;

    private int usdRate = 29;

    @Scheduled(fixedDelayString = "${notifications.rates-updates.frequency}")
    public void updateRatesAndNotify() {
        List<ValuteRateDto> updates = rateService.updateRates();

        if (updateRatesTestNotifications && updates.isEmpty()) {
            if (usdRate >= 99) {
                usdRate = 29;
            }
            updates = List.of(
                    new ValuteRateDto(USD.getIsoCode(), USD.toString(), "Тестовый доллар",
                            1, BigDecimal.valueOf(usdRate), LocalDate.now())
            );
            usdRate++;
        }

        messagingTemplate.convertAndSend(updates);
    }
}
