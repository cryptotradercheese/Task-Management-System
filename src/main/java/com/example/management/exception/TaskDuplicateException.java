package com.example.management.exception;

public class TaskDuplicateException extends RuntimeException {
    public TaskDuplicateException() {}

    public TaskDuplicateException(String message) {
        super(message);
    }
}
