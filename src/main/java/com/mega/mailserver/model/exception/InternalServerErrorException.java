package com.mega.mailserver.model.exception;


import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends HttpException {

    public InternalServerErrorException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
