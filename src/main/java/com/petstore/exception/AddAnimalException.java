package com.petstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AddAnimalException extends RuntimeException {
    public AddAnimalException(String message) {
        super(message);
    }
}
