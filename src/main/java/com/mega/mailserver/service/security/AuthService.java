package com.mega.mailserver.service.security;

import com.mega.mailserver.model.domain.UserK;
import com.mega.mailserver.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserService userService;

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public UserK getUser() {
        return (UserK)getAuthentication().getPrincipal();
    }
}
