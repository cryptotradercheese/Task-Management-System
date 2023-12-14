package com.example.management.repository;

import com.example.management.model.Priority;
import com.example.management.model.Status;
import com.example.management.model.Task;
import com.example.management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.status IN :statuses AND t.priority IN :priorities")
    Page<Task> findAll(Pageable pageable, @Param("statuses") Set<Status> statuses,
                       @Param("priorities") Set<Priority> priorities);

//    @Query(value = "SELECT u.authorTasks FROM User u JOIN Task t ON u = t.author " +
//            "WHERE u.email = :email AND t.status IN :statuses AND t.priority IN :priorities",
//            countQuery = "SELECT COUNT(u.authorTasks) FROM User u JOIN Task t ON u = t.author " +
//                    "WHERE u.email = :email AND t.status IN :statuses AND t.priority IN :priorities")
    @Query(value = "SELECT t FROM User u JOIN u.authorTasks t " +
            "WHERE u.email = :email AND t.status IN :statuses AND t.priority IN :priorities")
    Page<Task> findAllByAuthorEmail(@Param("email") String email, Pageable pageable,
                                    @Param("statuses") Set<Status> statuses,
                                    @Param("priorities") Set<Priority> priorities);

    @Transactional(readOnly = true)
    @Query("SELECT t FROM User u JOIN u.executorTasks t " +
            "WHERE u.email = :email AND t.status IN :statuses AND t.priority IN :priorities")
    Page<Task> findAllByExecutorEmail(@Param("email") String email, Pageable pageable,
                                      @Param("statuses") Set<Status> statuses,
                                      @Param("priorities") Set<Priority> priorities);

    @Transactional
    int deleteByName(String name);

    @Transactional(readOnly = true)
    boolean existsByName(String name);

    @Transactional(readOnly = true)
    Optional<Task> findByName(String name);

    @Transactional(readOnly = true)
    @Query("SELECT COUNT(e) = 1 FROM Task t JOIN t.executors e WHERE t.name = :taskName AND e.email = :executorEmail")
    boolean isTaskExecutor(@Param("taskName") String taskName, @Param("executorEmail") String executorEmail);
}
