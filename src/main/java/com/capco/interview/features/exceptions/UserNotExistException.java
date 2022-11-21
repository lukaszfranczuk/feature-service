package com.capco.interview.features.exceptions;

public class UserNotExistException extends RuntimeException {

    public UserNotExistException(String message) {
        super(message);
    }
}
