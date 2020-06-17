package com.jadajabajava.exceptions;

public class CommonServiceException extends Exception {
    public CommonServiceException(String message) {
        super(message);
    }

    public CommonServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
