package com.example.management.dto;

import com.example.management.model.Priority;
import com.example.management.model.Status;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TaskResponseDto {
    @JsonProperty
    private final String name;

    @JsonProperty
    private final String description;

    @JsonProperty
    private final Status status;

    @JsonProperty
    private final Priority priority;

    @JsonProperty
    private final String author;

    @JsonProperty
    private final List<String> executors;

    @JsonProperty
    private final List<CommentResponseDto> comments;

    public TaskResponseDto(String name, String description, Status status, Priority priority, String author, List<String> executors, List<CommentResponseDto> comments) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.author = author;
        this.executors = executors;
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getAuthor() {
        return author;
    }

    public List<String> getExecutors() {
        return executors;
    }

    public List<CommentResponseDto> getComments() {
        return comments;
    }
}
