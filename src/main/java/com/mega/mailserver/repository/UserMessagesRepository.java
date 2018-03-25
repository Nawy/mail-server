package com.mega.mailserver.repository;

import com.mega.mailserver.model.UserMessages;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserMessagesRepository extends CrudRepository<UserMessages, String> {
    Optional<UserMessages> findByOwnerEmail(String ownerEmail);
}
