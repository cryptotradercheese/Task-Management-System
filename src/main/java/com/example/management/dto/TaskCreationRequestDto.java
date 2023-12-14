package com.example.management.dto;


import com.example.management.model.Priority;
import com.example.management.model.Status;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

public class TaskCreationRequestDto {
    @Schema(description = "Task's name")
    @JsonProperty
    @NotEmpty
    private final String name;

    @Schema(description = "Task's description")
    @JsonProperty
    @NotEmpty
    private final String description;

    @Schema(description = "Task's status")
    @JsonProperty
    @NotNull
    private final Status status;

    @Schema(description = "Task's priority")
    @JsonProperty
    @NotNull
    private final Priority priority;

    @Schema(description = "Task's executors")
    @JsonProperty
    @NotNull
    private final List<@Email String> executors;

    @JsonCreator
    public TaskCreationRequestDto(@JsonProperty("name") String name,
                                  @JsonProperty("description") String description,
                                  @JsonProperty("status") Status status,
                                  @JsonProperty("priority") Priority priority,
                                  @JsonProperty("executors") List<@Email String> executors) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.executors = executors;
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

    public List<String> getExecutors() {
        return executors;
    }
}
