package com.project.Todo.controller;

import com.project.Todo.dto.TodoDTO;
import com.project.Todo.entity.Todo;
import com.project.Todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todo/")
public class TodoAPI {
    private final TodoService todoService;

    @Autowired
    public TodoAPI(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity getTodo()
    {
        return ResponseEntity.ok(todoService.getAll());
    }

    @PostMapping
    public ResponseEntity createTodo(@Valid @RequestBody TodoDTO todoDTO)
    {
        Todo todo=todoService.createTodo(todoDTO);
        return ResponseEntity.ok(todo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTodo(@PathVariable Long id) {
        Todo todo=todoService.deleteTodo(id);
        return ResponseEntity.ok(todo);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTodo(@PathVariable Long id, @Valid @RequestBody TodoDTO todoDTO) {
        Todo todo = todoService.updateTodoById(id, todoDTO);
        return ResponseEntity.ok(todo);
    }

    @PutMapping("/setcomplete/{id}")
    public ResponseEntity updateTodoComplete(@PathVariable Long id) {
        Todo todo = todoService.updateTodoComplete(id);
        return ResponseEntity.ok(todo);
    }
}
