package com.mega.mailserver.repository;

import com.mega.mailserver.model.domain.UserK;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserK, String> {

    Optional<UserK> findByName(String email);
}
