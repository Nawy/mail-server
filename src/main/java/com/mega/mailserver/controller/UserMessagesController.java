package com.mega.mailserver.controller;

import com.mega.mailserver.service.UserMessagesService;
import com.mega.mailserver.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserMessagesController {

    private final UserMessagesService userMessagesService;



}
