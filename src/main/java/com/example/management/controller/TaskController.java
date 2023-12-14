package com.example.management.controller;

import com.example.management.dto.*;
import com.example.management.model.Priority;
import com.example.management.model.Status;
import com.example.management.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Tag(name = "Tasks operations", description = "No users registration! A set of 10 predefined users is used. " +
        "User's email: 'user{i}@mail.com', user's password: 'password{i}' where i is from 1 to 10. For example, 'user1@mail.com', 'password1'.")
@RestController
@RequestMapping(path = "/api/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class TaskController {
    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Returns a task by name",
            parameters = {@Parameter(name = "name", description = "Unique task's name to return a task")},
            responses = {
                @ApiResponse(description = "Incorrect parameters", responseCode = "400",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @GetMapping(path = "/{name}")
    public TaskResponseDto getTask(@PathVariable("name") String taskName) {
        return taskService.getTask(taskName);
    }

    @Operation(summary = "Returns tasks utilizing filters and pagination",
        description = "if author and executor are not set, returns all users, else returns author's tasks or tasks of an executor. " +
                "Filters by status and/or priority. If a filter is not set, no filtering takes place",
        parameters = {
            @Parameter(name = "author", required = false, description = "Task's creator being used to find all created tasks"),
            @Parameter(name = "executor", required = false, description = "Task's executor being used to find all tasks he is doing"),
            @Parameter(name = "page", description = "Page number. Max 5 tasks a page"),
            @Parameter(name = "status", description = "Task's status being used for filtering"),
            @Parameter(name = "priority", description = "Task's status being used for filtering")
        },
        responses = {
            @ApiResponse(description = "Incorrect parameters", responseCode = "400",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)))
        }
    )
    @GetMapping(path = "")
    public List<TaskResponseDto> getTasks(
            @RequestParam(name = "author", defaultValue = "") String authorEmail,
            @RequestParam(name = "executor", defaultValue = "") String executorEmail,
            @RequestParam(value = "page", defaultValue = "1") int pageNumber,
            @RequestParam(value = "status", defaultValue = "") Set<Status> statuses,
            @RequestParam(value = "priority", defaultValue = "") Set<Priority> priorities) {
        if (statuses.isEmpty()) {
            statuses.addAll(Arrays.asList(Status.values()));
        }
        if (priorities.isEmpty()) {
            priorities.addAll(Arrays.asList(Priority.values()));
        }

        if (!authorEmail.isEmpty()) {
            return taskService
                    .getTasksByAuthorEmail(authorEmail, pageNumber - 1, statuses, priorities);
        } else if (!executorEmail.isEmpty()) {
            return taskService
                    .getTasksByExecutorEmail(executorEmail, pageNumber - 1, statuses, priorities);
        } else {
            return taskService.getTasks(pageNumber - 1, statuses, priorities);
        }
    }

    @Operation(summary = "Creates a task",
            responses = {
                @ApiResponse(description = "Incorrect parameters", responseCode = "400",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createTask(@RequestBody @Valid TaskCreationRequestDto request,
                           @AuthenticationPrincipal UserDetails userDetails) {
        taskService.createTask(request, userDetails.getUsername());
    }

    @Operation(summary = "Modifies a task",
            description = "If status and/or priority are not set the property is not modified, else updates the properties",
            parameters = {
                @Parameter(name = "name", description = "Task's name being modified"),
                @Parameter(name = "status", description = "New task's status. Only the creator and executors can modify"),
                @Parameter(name = "priority", description = "New task's priority. Only the creator can modify")
            },
            responses = {
                @ApiResponse(description = "Incorrect parameters", responseCode = "400",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponseDto.class))
                ),
                @ApiResponse(description = "No authority for such a modification", responseCode = "403",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponseDto.class))
                )
            }
    )
    @PatchMapping(path = "/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTask(@PathVariable("name") String taskName,
                           @RequestParam(name = "status", defaultValue = "") Status status,
                           @RequestParam(name = "priority", defaultValue = "") Priority priority,
                           @AuthenticationPrincipal UserDetails userDetails) {
        taskService.updateTask(userDetails.getUsername(), taskName, status, priority);
    }

    @Operation(summary = "Adds a comment for a task",
            parameters = {@Parameter(name = "name", description = "Task's name to add a comment")},
            responses = {
                @ApiResponse(description = "Incorrect parameters", responseCode = "400",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @PostMapping(path = "/{name}/comments", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addComment(@PathVariable("name") String taskName,
                           @RequestBody @Valid CommentRequestDto request) {
        taskService.addComment(taskName, request.getText());
    }

    @Operation(summary = "Adds an executor for a task",
            parameters = {@Parameter(name = "name", description = "Task's name to add an executor")},
            responses = {
                @ApiResponse(description = "Incorrect parameters", responseCode = "400",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponseDto.class))
                ),
                @ApiResponse(description = "No authority to add an executor", responseCode = "403",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponseDto.class))
                )
            }
    )
    @PostMapping(path = "/{name}/executors", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addExecutor(@PathVariable("name") String taskName,
                            @RequestBody @Valid ExecutorRequestDto executor,
                            @AuthenticationPrincipal UserDetails userDetails) {
        taskService.addExecutor(userDetails.getUsername(), taskName, executor.getEmail());
    }

    @Operation(summary = "Deletes a task",
            parameters = {@Parameter(name = "name", description = "Task's name to delete")},
            responses = {
                @ApiResponse(description = "Incorrect parameters", responseCode = "400",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponseDto.class))
                ),
                @ApiResponse(description = "No authority to delete a task", responseCode = "403",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponseDto.class))
                )
            }
    )
    @DeleteMapping(path = "/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable("name") String name,
                           @AuthenticationPrincipal UserDetails userDetails) {
        taskService.deleteTask(userDetails.getUsername(), name);
    }
}
