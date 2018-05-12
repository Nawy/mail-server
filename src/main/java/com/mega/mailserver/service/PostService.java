package com.mega.mailserver.service;

import com.mega.mailserver.config.EmailProperties;
import com.mega.mailserver.model.EmailAddress;
import com.mega.mailserver.model.domain.Letter;
import com.mega.mailserver.model.domain.User;
import com.mega.mailserver.model.enums.LetterDirection;
import com.mega.mailserver.model.exception.InternalServerErrorException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;

@Service
@AllArgsConstructor
public class PostService {

    private final SmtpService smtpService;
    private final MailboxService mailboxService;
    private final EmailProperties emailProperties;

    public void send(final Letter letter, final User user) throws AddressException {

        final EmailAddress toAddress = new EmailAddress(letter.getAddress());
        final String recipientDomain = toAddress.getDomain();

        if (!isLocalDomain(recipientDomain)) {
            try {
                smtpService.send(letter, user);
            } catch (Exception e) {
                throw new InternalServerErrorException("Cannot send message");
            }
        }

        final Letter outboxEmail = letter.toBuilder()
                .direction(LetterDirection.OUTBOX)
                .build();

        mailboxService.put(outboxEmail, user.getName());
    }

    private boolean isLocalDomain(final String domain) {
        return domain.equalsIgnoreCase(emailProperties.getDomain());
    }
}
