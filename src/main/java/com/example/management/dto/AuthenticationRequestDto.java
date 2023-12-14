package com.example.management.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class AuthenticationRequestDto {
    @Schema(description = "User's email")
    @JsonProperty
    @NotEmpty
    @Email
    private final String email;

    @Schema(description = "User's password")
    @JsonProperty
    @NotEmpty
    private final String password;

    @JsonCreator
    public AuthenticationRequestDto(@JsonProperty("email") String email,
                                    @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
