package com.mega.mailserver.config;

import com.mega.mailserver.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Value("${email.domain")
    private String domain;

    @Value("${email.smtp.host}")
    private String host;

    @Value("${email.smtp.port}")
    private int port;

    @Value("${email.smtp.username}")
    private String username;

    @Value("${email.smtp.username}")
    private String password;

    public Email getEmail(final String sender, final String fullName) {

        final Email email = new SimpleEmail();
        email.setHostName(host);
        email.setSmtpPort(port);
        email.setAuthenticator(new DefaultAuthenticator(username, password));

        final String emailAddress = String.format("%s@%s", sender, domain);

        try {
            if(StringUtils.isBlank(fullName)) {
                email.setFrom(emailAddress);
            } else {
                email.setFrom(emailAddress, fullName);
            }
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        return email;
    }
}
