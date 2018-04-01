package com.mega.mailserver.service.security;

import com.mega.mailserver.model.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public User getUser() {
        return (User)getAuthentication().getPrincipal();
    }
}
