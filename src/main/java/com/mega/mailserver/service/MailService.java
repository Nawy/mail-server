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

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
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
    public void receive(final Message message) throws Exception {

        MimeMessage mimeMessage = parseMimeMessage(message);
        ReceiveEmailDto receiveEmail = parseContent(mimeMessage);

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

        try {
            final String content = formatContent(parser);
            return ReceiveEmailDto.builder()
                    .from(parser.getFrom())
                    .text(content)
                    .build();
        } catch (Exception e) {
            log.error("Cannot get parsed email content", e);
            throw new RuntimeException(e);
        }
    }

    private String formatContent(final MimeMessageParser parser) {
        if (StringUtils.isBlank(parser.getPlainContent())) {
            String html = parser.getHtmlContent();
            return Jsoup.clean(html, Whitelist.basic());
        } else {
            return parser.getPlainContent();
        }
    }
}
