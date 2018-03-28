package com.mega.mailserver.service;

import com.mega.mailserver.config.EmailConfig;
import com.mega.mailserver.model.ReceiveEmailDto;
import com.mega.mailserver.model.SendEmailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final EmailConfig emailConfig;

    public void send(final SendEmailDto message) throws Exception {
        Email email = emailConfig.getEmail(message.getSender(), message.getFullName());

        final String recipientString = String.join(",",message.getTo());
        final List<InternetAddress> recipients = Arrays.asList(InternetAddress.parse(recipientString));

        email.setTo(recipients);
        email.setMsg(message.getText());
        email.send();
        log.info("[{}] sent mail to {}", message.getSender(), recipients);
    }

    @RabbitListener(queues = "haraka.emails")
    public void receive(final Message message) {

        MimeMessage mimeMessage = parseMimeMessage(message);
        ReceiveEmailDto receiveEmail = parseContent(mimeMessage);

        if(Objects.isNull(receiveEmail)) {
            return;
        }

        // TODO save
        log.info("From: {}, Message: {}", receiveEmail.getFrom(), receiveEmail.getText());
    }

    private MimeMessage parseMimeMessage(final Message message) {
        Session s = Session.getInstance(new Properties());
        InputStream is = new ByteArrayInputStream(message.getBody());

        try {
            return new MimeMessage(s, is);
        } catch (MessagingException e) {
            log.error("Cannot parse email message: {}", message, e);
            throw new RuntimeException(e);
        }
    }

    private ReceiveEmailDto parseContent(final MimeMessage mimeMessage) {
        MimeMessageParser parser = new MimeMessageParser(mimeMessage);

        try {
            parser.parse();
        } catch (Exception e) {
            log.error("Cannot parse email content", e);
            throw new RuntimeException(e);
        }

        final String content = cleanContent(parser);

        if(Objects.isNull(content)) {
            return null;
        }

        List<Address> to;
        try {
            to = parser.getTo();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String from;
        try {
            from = parser.getFrom();
        } catch (Exception e) {
            log.error("Cannot get parsed email content", e);
            throw new RuntimeException(e);
        }

        return ReceiveEmailDto.builder()
                .from(from)
                .text(content)
                .build();
    }

    private String cleanContent(final MimeMessageParser parser) {
        if (StringUtils.isBlank(parser.getPlainContent()) && StringUtils.isNotBlank(parser.getHtmlContent())) {
            String html = parser.getHtmlContent();
            return Jsoup.clean(html, Whitelist.basic());
        } else if(StringUtils.isBlank(parser.getPlainContent())) {
            return parser.getPlainContent();
        }
        return null;
    }
}
