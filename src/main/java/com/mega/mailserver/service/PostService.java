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

        final EmailAddress fromAddress = new EmailAddress(user.getName(), emailProperties.getDomain());
        final EmailAddress toAddress = new EmailAddress(letter.getAddress());
        final String recipientDomain = toAddress.getDomain();

        final Letter outboxEmail = letter.toBuilder()
                .direction(LetterDirection.OUTBOX)
                .build();

        mailboxService.put(outboxEmail, user.getName());

        if (!isLocalDomain(recipientDomain)) {
            smtpService.send(letter, user);
        }
        else {
            letter.setAddress(fromAddress.getAddress().toUnicodeString());
            letter.setDirection(LetterDirection.INBOX);
            mailboxService.put(letter, toAddress.getName());
        }
    }

    private boolean isLocalDomain(final String domain) {
        return domain.equalsIgnoreCase(emailProperties.getDomain());
    }
}
