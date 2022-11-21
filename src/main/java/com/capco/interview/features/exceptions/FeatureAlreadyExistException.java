package com.capco.interview.features.exceptions;

public class FeatureAlreadyExistException extends RuntimeException {

    public FeatureAlreadyExistException(String message) {
        super(message);
    }
}
