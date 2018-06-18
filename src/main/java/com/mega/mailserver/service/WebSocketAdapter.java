package com.mega.mailserver.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketAdapter {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/all")
    public void recieve(@Payload Map<String, String> message) throws Exception {
        log.info("test message: " + message);
    }


    public void send(String message) {
        simpMessagingTemplate.convertAndSend("/topics/all", new ObjectDto(message));
    }

    @Data
    @AllArgsConstructor
    private static class ObjectDto{
        private String message;
    }
}
