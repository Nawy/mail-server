package com.mega.mailserver.model.exception;


import org.springframework.http.HttpStatus;

public class BadRequestException extends HttpException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
