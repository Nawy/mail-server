package com.mega.mailserver.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HttpException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public HttpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
