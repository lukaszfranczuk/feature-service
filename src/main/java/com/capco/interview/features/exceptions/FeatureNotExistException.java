package com.capco.interview.features.exceptions;

public class FeatureNotExistException extends RuntimeException {

    public FeatureNotExistException(String message) {
        super(message);
    }
}
