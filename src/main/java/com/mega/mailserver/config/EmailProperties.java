package com.mega.mailserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

    private String domain;
    private String host;
    private int port;
    private String username;
    private String password;
}
