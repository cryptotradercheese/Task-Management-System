package com.example.management.service;

import com.example.management.exception.NoAuthorityException;
import com.example.management.exception.TaskDuplicateException;
import com.example.management.exception.TaskNotFoundException;
import com.example.management.dto.CommentResponseDto;
import com.example.management.dto.TaskCreationRequestDto;
import com.example.management.dto.TaskResponseDto;
import com.example.management.model.*;
import com.example.management.repository.TaskRepository;
import com.example.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private TaskRepository taskRepository;
    private UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createTask(TaskCreationRequestDto request, String authorEmail) {
        if (taskRepository.existsByName(request.getName())) {
            throw new TaskDuplicateException("Task '" + request.getName() + "' already exists");
        }
        User user = userRepository.findByEmail(authorEmail).get();

        Task task = new Task();
        task.setAuthor(user);
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        request.getExecutors().stream()
                .map(e -> userRepository.findByEmail(e).orElseThrow(() -> usernameNotFoundException(e)))
                .forEach(task::addExecutors);

        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(String requestInitializerEmail, String taskName) {
        String authorEmail = taskRepository.findByName(taskName)
                .orElseThrow(() -> taskNotFoundException(taskName)).getAuthor().getEmail();
        if (!authorEmail.equals(requestInitializerEmail)) {
            throw new NoAuthorityException("Only task's author can delete the task");
        }
        taskRepository.deleteByName(taskName);
    }

    @Transactional
    public void addComment(String taskName, String text) {
        Task task = taskRepository.findByName(taskName).orElseThrow(() -> taskNotFoundException(taskName));
        Comment comment = new Comment();
        comment.setAuthor(task.getAuthor());
        comment.setText(text);
        task.addComments(comment);
        taskRepository.save(task);
    }

    @Transactional
    public void addExecutor(String requestInitializerEmail, String taskName, String executorEmail) {
        Task task = taskRepository.findByName(taskName).orElseThrow(() -> taskNotFoundException(taskName));
        if (!task.getAuthor().getEmail().equals(requestInitializerEmail)) {
            throw new NoAuthorityException("Only task's author can add executors");
        }

        User executor = userRepository.findByEmail(executorEmail)
                .orElseThrow(() -> usernameNotFoundException(executorEmail));
        task.addExecutors(executor);
        taskRepository.save(task);
    }

    @Transactional
    public void updateTask(String requestInitializerEmail, String taskName,
                           Status status, Priority priority) {
        Task task = taskRepository.findByName(taskName).orElseThrow(() -> taskNotFoundException(taskName));
        boolean isTaskAuthor = task.getAuthor().getEmail().equals(requestInitializerEmail);
        if (priority != null) {
            if (!isTaskAuthor) {
                throw new NoAuthorityException("Only task's author can update the priority");
            }
            task.setPriority(priority);
        }

        boolean isTaskExecutor = taskRepository.isTaskExecutor(taskName, requestInitializerEmail);
        if (status != null) {
            if (!isTaskExecutor && !isTaskAuthor) {
                throw new NoAuthorityException("Only task's author and executors can update the status");
            }
            task.setStatus(status);
        }

        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public TaskResponseDto getTask(String taskName) {
        Task task = taskRepository.findByName(taskName).orElseThrow(() -> taskNotFoundException(taskName));
        return mapToTaskResponseDto(Arrays.asList(task)).get(0);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasks(int pageNumber, Set<Status> statuses,
                                          Set<Priority> priorities) {
        Page<Task> page = taskRepository
                .findAll(PageRequest.of(pageNumber, 5), statuses, priorities);
        return mapToTaskResponseDto(page.getContent());
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByAuthorEmail(String email, int pageNumber,
                                                       Set<Status> statuses, Set<Priority> priorities) {
        if (!userRepository.existsByEmail(email)) {
            throw usernameNotFoundException(email);
        }
        Page<Task> page = taskRepository
                .findAllByAuthorEmail(email, PageRequest.of(pageNumber, 5), statuses, priorities);
        return mapToTaskResponseDto(page.getContent());
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByExecutorEmail(String email, int pageNumber,
                                                         Set<Status> statuses, Set<Priority> priorities) {
        if (!userRepository.existsByEmail(email)) {
            throw usernameNotFoundException(email);
        }
        Page<Task> page = taskRepository
                .findAllByExecutorEmail(email, PageRequest.of(pageNumber, 5), statuses, priorities);
        return mapToTaskResponseDto(page.getContent());
    }

    private List<TaskResponseDto> mapToTaskResponseDto(List<Task> tasks) {
        List<TaskResponseDto> responseDtos = new ArrayList<>();
        for (Task task : tasks) {
            List<String> executors = task.getExecutors().stream()
                    .map(User::getEmail).collect(Collectors.toList());
            List<CommentResponseDto> comments = task.getComments().stream()
                    .map(c -> new CommentResponseDto(c.getAuthor().getEmail(), c.getText()))
                    .collect(Collectors.toList());
            TaskResponseDto responseDto = new TaskResponseDto(task.getName(), task.getDescription(),
                    task.getStatus(), task.getPriority(), task.getAuthor().getEmail(), executors, comments);
            responseDtos.add(responseDto);
        }

        return responseDtos;
    }

    private UsernameNotFoundException usernameNotFoundException(String username) {
        return new UsernameNotFoundException("User '" + username + "' is not found");
    }

    private TaskNotFoundException taskNotFoundException(String taskName) {
        return new TaskNotFoundException("Task '" + taskName + "' is not found");
    }
}
