package com.mega.mailserver.service;

import com.mega.mailserver.model.domain.UserK;
import com.mega.mailserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserK upsert(UserK user) {
        final String encodedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    public UserK get(String name){
        return userRepository.findByName(name).orElse(null);
    }
}
