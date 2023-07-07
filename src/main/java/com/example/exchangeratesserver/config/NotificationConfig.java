package com.example.exchangeratesserver.config;

import com.example.exchangeratesserver.constant.DestinationPrefixes;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class NotificationConfig {
    private final SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    private void setDefaultDestination() {
        messagingTemplate.setDefaultDestination(DestinationPrefixes.NOTIFICATIONS + "/rates-updates");
    }
}
