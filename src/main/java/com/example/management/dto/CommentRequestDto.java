package com.example.management.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public class CommentRequestDto {
    @Schema(description = "Comment's text")
    @JsonProperty
    @NotEmpty
    private final String text;

    @JsonCreator
    public CommentRequestDto(@JsonProperty("text") String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
