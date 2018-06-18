package com.mega.mailserver.controller;

import com.mega.mailserver.service.WebSocketAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final WebSocketAdapter socketAdapter;

    @GetMapping("/send/{message}")
    public void send(@PathVariable("message") String message) {
        socketAdapter.send(message);
    }

}
