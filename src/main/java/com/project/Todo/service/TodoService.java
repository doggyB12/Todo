package com.project.Todo.service;

import com.project.Todo.dto.TodoDTO;
import com.project.Todo.entity.Todo;
import com.project.Todo.exception.NotFoundException;
import com.project.Todo.repository.TodoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    TodoRepository todoRepository;

    public List<Todo> getAll()
    {
        return todoRepository.findByStatusTrue();
    }

    public Todo createTodo(TodoDTO todoDTO)
    {
        Todo todo = new Todo();
        todo.setTitle(todoDTO.getTitle());
        todo.setCompleted(false);
        todo.setStatus(true);
        return todoRepository.save(todo);
    }

    public Todo deleteTodo(long id)
    {
        Todo todo=todoRepository.findTodoById(id).orElseThrow(() -> new NotFoundException("Not found"));
        todo.setStatus(false);
        return todoRepository.save(todo);
    }

    public Todo updateTodoById(Long id, TodoDTO todoDTO) {
        Todo todo = todoRepository.findTodoById(id).orElseThrow(() -> new NotFoundException("Not found"));
        todo.setTitle(todoDTO.getTitle());
        return todoRepository.save(todo);
    }

    public Todo updateTodoComplete(Long id)
    {
        Todo todo = todoRepository.findTodoById(id).orElseThrow(() -> new NotFoundException("Not found"));
        if(todo.isCompleted())
        {
            todo.setCompleted(false);
        }else
        {
            todo.setCompleted(true);
        }
        return todoRepository.save(todo);
    }

}
