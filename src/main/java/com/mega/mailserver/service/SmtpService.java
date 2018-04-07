package com.mega.mailserver.service;

import com.mega.mailserver.config.EmailConfig;
import com.mega.mailserver.model.ReceiveEmailDto;
import com.mega.mailserver.model.SendEmailDto;
import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.User;
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
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmtpService {

    private final EmailConfig emailConfig;
    private final MailboxService mailboxService;
    private final UserService userService;

    public void send(final Letter letter, final User user) throws Exception {
        Email email = emailConfig.getEmail(user.getName(), user.getFullName());

        final String recipientString = String.join(",", letter.getAddress());
        final List<InternetAddress> recipients = Arrays.asList(InternetAddress.parse(recipientString));

        email.setTo(recipients);
        email.setMsg(letter.getText());
        email.send();
        log.info("[{}] sent mail to {}", user.getName(), recipients);
    }

    @RabbitListener(queues = "haraka.emails")
    public void receive(final Message message) {

        MimeMessage mimeMessage = parseMimeMessage(message);
        ReceiveEmailDto receiveEmail = parseContent(mimeMessage);

        if (Objects.isNull(receiveEmail)) {
            return;
        }

        final List<User> recipients = findRecipients(receiveEmail.getRecipients());

        recipients.forEach(recipient -> mailboxService.put(receiveEmail.toLetter(), recipient));
        log.info("From: {}, Letter: {}", receiveEmail.getFrom(), receiveEmail.getText());
    }

    private List<User> findRecipients(final List<Address> emails) {

        final List<User> users = new ArrayList<>();
        for (Address address : emails) {
            final String addr = address.toString();
            final User user = userService.get(addr.substring(0, addr.indexOf("@")));
            if (Objects.nonNull(user)) {
                users.add(user);
            }
        }

        return users;
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

        final String content = cleanContent(parser.getHtmlContent(), parser.getPlainContent());

        if (Objects.isNull(content)) {
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
            log.error("Cannot getLetters parsed email content", e);
            throw new RuntimeException(e);
        }

        return ReceiveEmailDto.builder()
                .recipients(to)
                .from(from)
                .text(content)
                .build();
    }

    private String cleanContent(final String htmlContent, final String plainContent) {

        if (StringUtils.isBlank(plainContent) && StringUtils.isNotBlank(htmlContent)) {
            return Jsoup.clean(htmlContent, Whitelist.basic());
        } else if (StringUtils.isNotBlank(plainContent)) {
            return plainContent;
        }
        return null;
    }
}
