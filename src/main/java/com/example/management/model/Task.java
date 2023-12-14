package com.example.management.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "executors_tasks",
            joinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "executor_id", referencedColumnName = "id"))
    private Set<User> executors = new LinkedHashSet<>();

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Comment> comments = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        if (author == null) {
            if (this.author != null) {
                this.author.getAuthorTasks().remove(this);
            }
        } else {
            author.getAuthorTasks().add(this);
        }
        this.author = author;
    }

    public Set<User> getExecutors() {
        return Collections.unmodifiableSet(executors);
    }

    public void addExecutors(User... executors) {
        Objects.requireNonNull(executors);
        for (User executor : executors) {
            this.executors.add(executor);
            executor.getExecutorTasks().add(this);
        }
    }

    public void removeExecutors(User... executors) {
        Objects.requireNonNull(executors);
        for (User executor : executors) {
            this.executors.remove(executor);
            executor.getExecutorTasks().remove(this);
        }
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void addComments(Comment... comments) {
        Objects.requireNonNull(comments);
        for (Comment comment : comments) {
            this.comments.add(comment);
            comment.setTask(this);
        }
    }

    public void removeComments(Comment... comments) {
        Objects.requireNonNull(comments);
        for (Comment comment : comments) {
            this.comments.remove(comment);
            comment.setTask(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
