package com.project.commons.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * Created by user on 7:05 22/07/2024, 2024
 */
public class CustomExceptionHandler extends Exception implements Serializable {

    @Serial
    private static final long serialVersionUID = 3446654481762428477L;

    private final String message;
    private final HttpStatus httpStatus;

    public CustomExceptionHandler(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
