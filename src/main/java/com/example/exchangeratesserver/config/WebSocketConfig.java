package com.example.exchangeratesserver.config;

import com.example.exchangeratesserver.constant.AppPrefixes;
import com.example.exchangeratesserver.constant.DestinationPrefixes;
import com.example.exchangeratesserver.constant.StompPaths;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(DestinationPrefixes.NOTIFICATIONS);
        registry.setApplicationDestinationPrefixes(AppPrefixes.APP);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(StompPaths.WEBSOCKETS).setAllowedOriginPatterns("**").withSockJS();
    }
}
