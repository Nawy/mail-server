package com.mega.mailserver.repository;

import com.mega.mailserver.model.domain.Mailbox;
import org.springframework.data.repository.CrudRepository;

public interface MailboxRepository extends CrudRepository<Mailbox, String> {
}
