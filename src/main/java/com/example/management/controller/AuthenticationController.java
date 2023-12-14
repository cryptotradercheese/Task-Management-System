package com.example.management.controller;

import com.example.management.dto.AuthenticationRequestDto;
import com.example.management.dto.AuthenticationResponseDto;
import com.example.management.dto.ErrorResponseDto;
import com.example.management.repository.UserRepository;
import com.example.management.service.AuthenticationService;
import com.example.management.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication")
@RestController
@RequestMapping(path = "/api/auth", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class AuthenticationController {
    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Authenticates a user by the email and password and returns a JWT",
        responses = {
            @ApiResponse(description = "Bad Request", responseCode = "400",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(description = "Wrong password", responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class))
            )
        }
    )
    @PostMapping("/login")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody @Valid AuthenticationRequestDto request) {
        String jwt = authenticationService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(new AuthenticationResponseDto(jwt));
    }
}
