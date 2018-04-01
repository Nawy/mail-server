package com.mega.mailserver.repository;

import com.mega.mailserver.model.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByName(String email);
}
