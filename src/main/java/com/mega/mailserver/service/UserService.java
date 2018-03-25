package com.mega.mailserver.service;

import com.mega.mailserver.model.domain.User;
import com.mega.mailserver.model.exception.ForbiddenException;
import com.mega.mailserver.model.exception.NotFoundException;
import com.mega.mailserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User update(User user) {
        User oldUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("No user found for id: " + user.getId()));

        oldUser.setPassword(user.getPassword());
        oldUser.setPhoneNumber(user.getPhoneNumber());
        oldUser.setFullName(user.getFullName());

        return userRepository.save(user);
    }

    public User insert(User user) {
        boolean alreadyExists = userRepository.findByEmail(user.getEmail()).isPresent();
        if (alreadyExists) {
            throw new ForbiddenException(String.format("user with such email :'%s' already exists", user.getEmail()));
        }
        return userRepository.save(user);
    }

    public User get(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("no user for email: " + email));
    }
}
