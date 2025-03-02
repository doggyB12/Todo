package com.project.Todo.controller;


import com.project.Todo.dto.TodoDTO;
import com.project.Todo.entity.Todo;
import com.project.Todo.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class TodoAPITest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private TodoService todoService;

    @Autowired
    private Validator validator;

//---------------Get-----2 testcases--------------
    // Returns HTTP 200 OK status when successfully retrieving todos
    @Test
    public void test_get_todo_returns_ok_status() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        List<Todo> todos = Arrays.asList(new Todo());
        when(todoService.getAll()).thenReturn(todos);

        ResponseEntity response = todoAPI.getTodo();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(todos, response.getBody());
    }

    // Returns empty list when no active todos exist
    @Test
    public void test_get_todo_returns_empty_list_when_no_active_todos_exist() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        List<Todo> emptyTodos = Collections.emptyList();
        when(todoService.getAll()).thenReturn(emptyTodos);

        ResponseEntity response = todoAPI.getTodo();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyTodos, response.getBody());
    }




//---------------Create-----
    @Test
    public void test_create_todo_success() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("Test Todo");

        Todo expectedTodo = new Todo();
        expectedTodo.setTitle("Test Todo");
        expectedTodo.setCompleted(false);
        expectedTodo.setStatus(true);

        when(todoService.createTodo(todoDTO)).thenReturn(expectedTodo);

        ResponseEntity response = todoAPI.createTodo(todoDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTodo, response.getBody());
        verify(todoService).createTodo(todoDTO);
    }

    // Empty title in TodoDTO returns 400 Bad Request
    @Test
    public void test_create_todo_with_empty_title_returns_bad_request() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("");

        // Validate TodoDTO
        BindingResult bindingResult = new BeanPropertyBindingResult(todoDTO, "todoDTO");
        validator.validate(todoDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            ResponseEntity response = new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        } else {
            ResponseEntity response = todoAPI.createTodo(todoDTO);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    // Special characters in title returns 400 Bad Request
    @Test
    public void test_create_todo_with_special_characters_returns_bad_request() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("Invalid@Title!");

        // Validate TodoDTO
        BindingResult bindingResult = new BeanPropertyBindingResult(todoDTO, "todoDTO");
        validator.validate(todoDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            ResponseEntity response = new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        } else {
            ResponseEntity response = todoAPI.createTodo(todoDTO);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        verify(todoService, never()).createTodo(any(TodoDTO.class));
    }



}
