package com.jadajabajava.exceptions;

public class NotExistException extends Exception {

    public NotExistException(String message) {
        super(message);
    }

    public NotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
