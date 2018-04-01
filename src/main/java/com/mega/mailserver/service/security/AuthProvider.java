package com.mega.mailserver.service.security;

import com.mega.mailserver.model.domain.User;
import com.mega.mailserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String name = authentication.getName();
        final String password = authentication.getCredentials().toString();

        final User user = userService.get(name);
        if (Objects.nonNull(user) && isAuth(password, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(user, password);
        }

        return null;
    }

    private boolean isAuth(final String rawPassword, final String encodedPassowrd) {
        return passwordEncoder.matches(rawPassword, encodedPassowrd);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
