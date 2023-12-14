package com.example.management.exception;

public class NoAuthorityException extends RuntimeException {
    public NoAuthorityException() {}

    public NoAuthorityException(String message) {
        super(message);
    }
}
