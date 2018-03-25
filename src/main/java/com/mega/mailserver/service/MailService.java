package com.mega.mailserver.service;

import com.mega.mailserver.config.EmailConfig;
import com.mega.mailserver.model.ReceiveEmailDto;
import com.mega.mailserver.model.SendEmailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final EmailConfig emailConfig;

    public void send(final SendEmailDto message) {
        Email email = emailConfig.getEmail(message.getSender(), message.getFullName());

        final List<InternetAddress> recipients = message.getTo()
                .stream()
                .map(address -> {
                    try {
                        return InternetAddress.parse(address)[0];
                    } catch (AddressException e) {
                        log.error("Cannot parse recipients' emails", e);
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());

        try {
            email.setTo(recipients);
        } catch (EmailException e) {
            log.error("Cannot set recipients", e);
            throw new RuntimeException(e);
        }

        try {
            email.setMsg(message.getText());
        } catch (EmailException e) {
            log.error("Cannot set message", e);
            throw new RuntimeException(e);
        }

        try {
            email.send();
        } catch (EmailException e) {
            log.error("Cannot send email ", e);
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "haraka.emails")
    public void receive(final Message message) {
        Session s = Session.getInstance(new Properties());
        InputStream is = new ByteArrayInputStream(message.getBody());

        MimeMessage mail;
        try {
            mail = new MimeMessage(s, is);
        } catch (MessagingException e) {
            log.error("Cannot parse email message: {}", message, e);
            throw new RuntimeException(e);
        }
        MimeMessageParser parser = new MimeMessageParser(mail);

        try {
            parser.parse();
        } catch (Exception e) {
            log.error("Cannot parse email content", e);
            throw new RuntimeException(e);
        }

        ReceiveEmailDto receiveEmail;
        try {
            receiveEmail = ReceiveEmailDto.builder()
                    .from(parser.getFrom())
                    .text(getText(parser))
                    .build();
        } catch (Exception e) {
            log.error("Cannot get parsed email content", e);
            throw new RuntimeException(e);
        }


        log.info("From: {}, Message: {}", receiveEmail.getFrom(), receiveEmail.getText());
    }

    private String getText(final MimeMessageParser parser) {
        if(StringUtils.isBlank(parser.getPlainContent())) {
            String html = parser.getHtmlContent();
            return Jsoup.clean(html, Whitelist.basic());
        } else {
            return parser.getPlainContent();
        }
    }
}
