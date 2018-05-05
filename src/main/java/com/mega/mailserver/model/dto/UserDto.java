package com.mega.mailserver.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mega.mailserver.model.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class    UserDto {
    private String name;
    private String phoneNumber;
    private String fullName;
    private String password;

    public static UserDto valueOf(final User user) {
        return UserDto.builder()
                .name(user.getName())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public static UserDto nameValueOf(final User user) {
        return UserDto.builder()
                .name(user.getName())
                .build();
    }

    public User toUser() {
        return User.builder()
                .name(name)
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .password(password)
                .build();
    }
}
