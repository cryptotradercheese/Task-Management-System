package com.example.management.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentResponseDto {
    @JsonProperty
    private final String author;

    @JsonProperty
    private final String text;

    @JsonCreator
    public CommentResponseDto(String author, String text) {
        this.author = author;
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
