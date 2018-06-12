package com.mega.mailserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketAdapter {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/test")
    public void recieve() throws Exception {
        log.info("test message");
    }


    public void send(Object object) {
        simpMessagingTemplate.send("/test", new GenericMessage<>(object));

    }
}
