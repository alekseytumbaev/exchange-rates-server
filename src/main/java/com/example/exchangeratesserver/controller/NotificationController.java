package com.example.exchangeratesserver.controller;

import com.example.exchangeratesserver.model.ValuteRateDto;
import com.example.exchangeratesserver.service.ValuteRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ValuteRateService rateService;

    @Value("${notifications.rates-updates.test-notification}")
    private boolean updateRatesTestNotifications;

    @Scheduled(fixedDelayString = "${notifications.rates-updates.frequency}")
    public void updateRatesAndNotify() {
        List<ValuteRateDto> updates = rateService.updateRates();

        if (updateRatesTestNotifications && updates.isEmpty()) {
            messagingTemplate.convertAndSend("Тестовое уведомление (°▽°)/");
        }

        if (!updates.isEmpty()) {
            messagingTemplate.convertAndSend(updates);
        }
    }
}
