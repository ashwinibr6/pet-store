package com.petstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(AddAnimalException.class)
    public ResponseEntity<String> handleAddAnimalException(AddAnimalException exception) {
        return new ResponseEntity<String>(exception.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }
}
