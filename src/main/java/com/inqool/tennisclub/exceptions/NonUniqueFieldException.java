package com.inqool.tennisclub.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NonUniqueFieldException extends RuntimeException {
    public NonUniqueFieldException(String message) {
        super(message);
    }
}
