package com.example.management.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

public class ExecutorRequestDto {
    @Schema(description = "Executor's email")
    @JsonProperty
    @Email
    private final String email;

    @JsonCreator
    public ExecutorRequestDto(@JsonProperty("email") String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
