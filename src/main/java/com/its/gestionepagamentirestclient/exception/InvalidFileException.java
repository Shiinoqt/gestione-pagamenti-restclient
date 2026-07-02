package com.its.gestionepagamentirestclient.exception;

import org.springframework.http.HttpStatus;

public class InvalidFileException extends AppException {
    public InvalidFileException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
