package com.mega.mailserver.service;

import com.mega.mailserver.model.User;
import com.mega.mailserver.model.exception.NotFoundException;
import com.mega.mailserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User save(User user){
        return userRepository.save(user);
    }

    public User get(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("no user for email: " + email));
    }


}
