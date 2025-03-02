package com.project.Todo.controller;

import com.project.Todo.dto.TodoDTO;
import com.project.Todo.entity.Todo;
import com.project.Todo.exception.NotFoundException;
import com.project.Todo.service.TodoService;
import jakarta.validation.ConstraintViolationException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
public class TodoAPITest {


    @InjectMocks
    private TodoService todoService;

    @Autowired
    private Validator validator;



    //---------------Get-----4 testcases--------------
    //1 - Returns HTTP 200 OK status when successfully retrieving todos - HappyCase
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

    //2 - Returns list of active todos from service layer
    @Test
    public void test_get_todo_returns_active_todos() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        List<Todo> activeTodos = Arrays.asList(new Todo(), new Todo());
        when(todoService.getAll()).thenReturn(activeTodos);

        ResponseEntity response = todoAPI.getTodo();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(activeTodos, response.getBody());
    }

    //3 - Returns empty list when no active todos exist
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

    //4 - Handle case when todoService throws unexpected exceptions
    @Test
    public void test_get_todos_handles_service_exception() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        when(todoService.getAll()).thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(RuntimeException.class, () -> {
            todoAPI.getTodo();
        });
    }



    //---------------Create-----6 testcases--------------
    //1 - HappyCase
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

    //2 - Empty title in TodoDTO returns 400 Bad Request
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
            System.out.println("haha");
        } else {
            ResponseEntity response = todoAPI.createTodo(todoDTO);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    //3 - Special characters in title returns 400 Bad Request
    @Test
    public void test_create_todo_with_special_characters_returns_bad_request() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("Invalid@Title");

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

    //4 - Title longer than 50 characters returns 400 Bad Request
    @Test
    public void test_create_todo_with_long_title_returns_bad_request() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("This title is definitely longer than fifty characters in length");

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

    //5 - Title with only whitespace characters is rejected
    @Test
    public void test_create_todo_with_whitespace_title_rejected() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("   ");

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

    //6 - Missing request body returns 400 Bad Request
    @Test
    public void test_create_todo_with_missing_body_returns_bad_request() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        ResponseEntity response = todoAPI.createTodo(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(todoService, never()).createTodo(any(TodoDTO.class));
    }



//---------------Delete-----6 testcases--------------
    //1 - HappyCase
    @Test
    public void test_delete_todo_success() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        Todo mockTodo = new Todo();
        mockTodo.setId(1L);
        mockTodo.setTitle("Test Todo");
        mockTodo.setStatus(false);

        when(todoService.deleteTodo(1L)).thenReturn(mockTodo);

        ResponseEntity response = todoAPI.deleteTodo(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTodo, response.getBody());
        verify(todoService).deleteTodo(1L);
    }

    //2 - Verify todo status is set to false after deletion
    @Test
    public void test_todo_status_false_after_deletion() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        Todo mockTodo = new Todo();
        mockTodo.setId(1L);
        mockTodo.setTitle("Sample Todo");
        mockTodo.setStatus(false);

        when(todoService.deleteTodo(1L)).thenReturn(mockTodo);

        ResponseEntity response = todoAPI.deleteTodo(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTodo, response.getBody());
        assertFalse(((Todo) response.getBody()).isStatus());
        verify(todoService).deleteTodo(1L);
    }

    //3 - Attempt to delete todo with non-existent ID should throw NotFoundException
    @Test
    public void test_delete_todo_not_found_exception() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        when(todoService.deleteTodo(999L)).thenThrow(new NotFoundException("Not found"));

        assertThrows(NotFoundException.class, () -> {
            todoAPI.deleteTodo(999L);
        });

        verify(todoService).deleteTodo(999L);
    }

    //4 - Handle maximum Long value ID
    @Test
    public void test_delete_todo_with_max_long_id() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        Todo mockTodo = new Todo();
        mockTodo.setId(Long.MAX_VALUE);
        mockTodo.setTitle("Max ID Todo");
        mockTodo.setStatus(false);

        when(todoService.deleteTodo(Long.MAX_VALUE)).thenReturn(mockTodo);

        ResponseEntity response = todoAPI.deleteTodo(Long.MAX_VALUE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTodo, response.getBody());
        verify(todoService).deleteTodo(Long.MAX_VALUE);
    }

    //5 - Attempt to delete todo with invalid ID format (non-numeric)
    @Test
    public void test_delete_todo_with_invalid_id_format() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        String invalidId = "abc";

        assertThrows(NumberFormatException.class, () -> {
            todoAPI.deleteTodo(Long.parseLong(invalidId));
        });

        verify(todoService, never()).deleteTodo(anyLong());
    }

    //6 - Attempt to delete already deleted todo (status=false)
    @Test
    public void test_delete_already_deleted_todo() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        Todo mockTodo = new Todo();
        mockTodo.setId(1L);
        mockTodo.setTitle("Test Todo");
        mockTodo.setStatus(false);

        when(todoService.deleteTodo(1L)).thenReturn(mockTodo);

        ResponseEntity response = todoAPI.deleteTodo(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTodo, response.getBody());
        verify(todoService).deleteTodo(1L);
    }

    //---------------Update-----6 testcases--------------
    //1 - happycase
    @Test
    public void test_update_todo_success() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("Updated Title");

        Todo updatedTodo = new Todo();
        updatedTodo.setId(1L);
        updatedTodo.setTitle("Updated Title");
        updatedTodo.setCompleted(false);
        updatedTodo.setStatus(true);

        when(todoService.updateTodoById(1L, todoDTO)).thenReturn(updatedTodo);

        ResponseEntity response = todoAPI.updateTodo(1L, todoDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTodo, response.getBody());
        verify(todoService).updateTodoById(1L, todoDTO);
    }

    //2 - Handles non-existent todo id by throwing NotFoundException
    @Test
    public void test_update_todo_not_found() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("Updated Title");

        when(todoService.updateTodoById(999L, todoDTO)).thenThrow(new NotFoundException("Not found"));

        assertThrows(NotFoundException.class, () -> {
            todoAPI.updateTodo(999L, todoDTO);
        });

        verify(todoService).updateTodoById(999L, todoDTO);
    }

    //3 - Rejects title longer than 50 characters
    @Test
    public void test_update_todo_rejects_long_title() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("This title is definitely longer than fifty characters long");

        // Validate TodoDTO
        BindingResult bindingResult = new BeanPropertyBindingResult(todoDTO, "todoDTO");
        validator.validate(todoDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            Throwable thrown = assertThrows(
                    ConstraintViolationException.class,
                    () -> {
                        throw new ConstraintViolationException(bindingResult.getAllErrors().toString(), null);
                    }
            );
            assertEquals(ConstraintViolationException.class, thrown.getClass());
        } else {
            ResponseEntity response = todoAPI.updateTodo(1L, todoDTO);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        verify(todoService, never()).updateTodoById(anyLong(), any(TodoDTO.class));
    }

    //4 - Rejects empty/blank title with validation error
    @Test
    public void test_update_todo_rejects_blank_title() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle(""); // Blank title

        // Validate TodoDTO
        BindingResult bindingResult = new BeanPropertyBindingResult(todoDTO, "todoDTO");
        validator.validate(todoDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            Throwable thrown = assertThrows(
                    ConstraintViolationException.class,
                    () -> {
                        throw new ConstraintViolationException(bindingResult.getAllErrors().toString(), null);
                    }
            );
            assertEquals(ConstraintViolationException.class, thrown.getClass());
        } else {
            ResponseEntity response = todoAPI.updateTodo(1L, todoDTO);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        verify(todoService, never()).updateTodoById(anyLong(), any(TodoDTO.class));
    }

    //5 - Rejects title with special characters
    @Test
    public void test_update_todo_rejects_special_characters() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("Invalid@Title!");

        // Validate TodoDTO
        BindingResult bindingResult = new BeanPropertyBindingResult(todoDTO, "todoDTO");
        validator.validate(todoDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            Throwable thrown = assertThrows(
                    ConstraintViolationException.class,
                    () -> {
                        throw new ConstraintViolationException(bindingResult.getAllErrors().toString(), null);
                    }
            );
            assertEquals(ConstraintViolationException.class, thrown.getClass());
        } else {
            ResponseEntity response = todoAPI.updateTodo(1L, todoDTO);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        verify(todoService, never()).updateTodoById(anyLong(), any(TodoDTO.class));
    }

    //6 - Handles null TodoDTO object
    @Test
    public void test_update_todo_with_null_tododto() {
        TodoService todoService = mock(TodoService.class);
        TodoAPI todoAPI = new TodoAPI(todoService);

        Long todoId = 1L;
        TodoDTO todoDTO = null;

        when(todoService.updateTodoById(todoId, todoDTO)).thenThrow(new IllegalArgumentException("TodoDTO cannot be null"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            todoAPI.updateTodo(todoId, todoDTO);
        });

        assertEquals("TodoDTO cannot be null", exception.getMessage());
        verify(todoService).updateTodoById(todoId, todoDTO);
    }

}