package com.example.exchangeratesserver.controller;


import com.example.exchangeratesserver.constant.DestinationPrefixes;
import com.example.exchangeratesserver.constant.StompPaths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class NotificationControllerTest {

    @Autowired
    private NotificationController notificationController;

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))
        ));
    }

    @Test
    @DisplayName("Должен возвращать тестовое уведомление")
    void updateRatesAndNotifyTestNotifications() throws Exception {
        ReflectionTestUtils.setField(notificationController, "updateRatesTestNotifications", true);

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);
        stompClient.setMessageConverter(new StringMessageConverter());
        StompSession session = stompClient
                .connectAsync("ws://localhost:" + port + StompPaths.WEBSOCKETS, new StompSessionHandlerAdapter() {
                })
                .get();

        session.subscribe(DestinationPrefixes.NOTIFICATIONS + "/rates-updates",
                new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        blockingQueue.add((String) payload);
                    }
                }
        );

        await().untilAsserted(() -> assertEquals("Тестовое уведомление (°▽°)/", blockingQueue.poll()));
    }
}