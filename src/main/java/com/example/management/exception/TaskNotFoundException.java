package com.example.management.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException() {}

    public TaskNotFoundException(String message) {
        super(message);
    }
}
