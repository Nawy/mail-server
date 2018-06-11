package com.mega.mailserver.service;

import com.mega.mailserver.config.EmailProperties;
import com.mega.mailserver.model.ReceiveEmailDto;
import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmtpService {

    private final EmailProperties emailProperties;
    private final MailboxService mailboxService;
    private final UserService userService;

    @Async
    public void send(final Letter letter, final User user) {
        try {
            final String recipientString = String.join(",", letter.getAddress());

            InternetAddress[] internetAddresses;
            try {
                internetAddresses = InternetAddress.parse(recipientString);
            } catch (AddressException e) {
                log.error("Can't parse recipient address: {}", recipientString);
                throw new RuntimeException(e);
            }
            final List<InternetAddress> recipients = Arrays.asList(internetAddresses);

            Email email = preConfigureEmail(user.getName(), user.getFullName());
            try {
                email.setTo(recipients);
                email.setMsg(letter.getText());
            } catch (EmailException e) {
                log.error("Can't create email to: {} with body from letter:{}", recipientString, letter.toString());
                throw new RuntimeException(e);
            }

            try {
                email.send();
            } catch (EmailException e) {
                log.error("Can't send email to: {}", recipientString);
                throw new RuntimeException(e);
            }
            log.info("[{}] sent mail to {}", user.getName(), recipients);
        } catch (Exception e) {
            mailboxService.setNotDelivered(letter, user.getName());
        }
    }

    @RabbitListener(queues = "haraka.emails")
    public void receive(final Message message) {

        MimeMessage mimeMessage = parseMimeMessage(message);
        ReceiveEmailDto receiveEmail = parseContent(mimeMessage);
        if (Objects.isNull(receiveEmail)) return;
        final List<User> recipients = findRecipients(receiveEmail.getRecipients());

        recipients.forEach(recipient -> mailboxService.put(receiveEmail.toLetter(), recipient.getName()));
        log.info("From: {}, Letter: {}", receiveEmail.getFrom(), receiveEmail.getText());
    }

    private List<User> findRecipients(final List<InternetAddress> emails) {

        final List<User> users = new ArrayList<>();
        for (InternetAddress address : emails) {
            final String addr = address.getAddress();
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

        final String text = cleanContent(parser.getHtmlContent(), parser.getPlainContent());
        final String htmlText = parser.getHtmlContent();

        if (Objects.isNull(text)) {
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

        String subject;
        try {
            subject = parser.getSubject();
        } catch (Exception e) {
            log.error("Cannot get subject", e);
            throw new RuntimeException(e);
        }

        return ReceiveEmailDto.builder()
                .recipients(to.stream().map(addr -> (InternetAddress)addr).collect(Collectors.toList()))
                .from(from)
                .subject(subject)
                .text(text)
                .htmlText(htmlText)
                .build();
    }

    private String cleanContent(final String htmlContent, final String plainContent) {

        if (StringUtils.isBlank(plainContent) && StringUtils.isNotBlank(htmlContent)) {
            return Jsoup.clean(htmlContent, Whitelist.simpleText());
        } else if (StringUtils.isNotBlank(plainContent)) {
            return plainContent;
        }
        return null;
    }

    private Email preConfigureEmail(final String sender, final String fullName) {

        final Email email = new SimpleEmail();
        email.setHostName(emailProperties.getHost());
        email.setSmtpPort(emailProperties.getPort());
        email.setAuthenticator(
                new DefaultAuthenticator(
                        emailProperties.getUsername(),
                        emailProperties.getPassword()
                )
        );

        final String emailAddress = String.format("%s@%s", sender, emailProperties.getDomain());

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
