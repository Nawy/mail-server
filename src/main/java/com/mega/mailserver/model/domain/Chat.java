package com.mega.mailserver.model.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {
    private String name;
    private LocalDateTime lastDeliveryDate;
    private int amountNew;

    @Builder.Default
    private List<Letter> messages = new ArrayList<>();
}
