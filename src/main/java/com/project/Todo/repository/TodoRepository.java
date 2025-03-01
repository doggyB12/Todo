package com.project.Todo.repository;

import com.project.Todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo,Long> {
    List<Todo> findByStatusTrue();
    Optional<Todo> findTodoById(long id);
}
