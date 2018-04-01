package com.mega.mailserver.model.dto;

import com.mega.mailserver.model.domain.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String name;
    private String phoneNumber;
    private String fullName;

    public static UserDto valueOf(final User user) {
        return UserDto.builder()
                .name(user.getName())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
