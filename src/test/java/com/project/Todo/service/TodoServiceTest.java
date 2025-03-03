package com.project.Todo.service;

import com.project.Todo.dto.TodoDTO;
import com.project.Todo.entity.Todo;
import com.project.Todo.exception.NotFoundException;
import com.project.Todo.repository.TodoRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.security.Provider;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Autowired
    private Validator validator;




    //----------Get----5 testcases------
    //1 - Happycase - Returns list of all Todo items with status=true
    @Test
    public void test_get_all_active_todos() {
        Todo todo1 = new Todo(1L, "Task 1", true, true);
        Todo todo2 = new Todo(2L, "Task 2", false, true);
        List<Todo> expectedTodos = Arrays.asList(todo1, todo2);

        when(todoRepository.findByStatusTrue()).thenReturn(expectedTodos);

        List<Todo> actualTodos = todoService.getAll();

        assertEquals(expectedTodos, actualTodos);
        verify(todoRepository).findByStatusTrue();
    }

    //2 - Happycase - Returns empty list when no todos with status=true
    @Test
    public void test_get_all_returns_empty_list_when_no_todos_with_status_true() {
        List<Todo> expectedTodos = Collections.emptyList();

        when(todoRepository.findByStatusTrue()).thenReturn(expectedTodos);

        List<Todo> actualTodos = todoService.getAll();

        assertEquals(expectedTodos, actualTodos);
        verify(todoRepository).findByStatusTrue();
    }

    //3 - Handle case when database connection fails
    @Test
    public void test_get_all_database_connection_failure() {

        when(todoRepository.findByStatusTrue()).thenThrow(new RuntimeException("Database connection failed"));

        assertThrows(RuntimeException.class, () -> {
            todoService.getAll();
        });

        verify(todoRepository).findByStatusTrue();
    }

    //4 - Handle case when Todo table is empty
    @Test
    public void test_get_all_when_todo_table_is_empty() {

        List<Todo> expectedTodos = Collections.emptyList();

        when(todoRepository.findByStatusTrue()).thenReturn(expectedTodos);

        List<Todo> actualTodos = todoService.getAll();

        assertEquals(expectedTodos, actualTodos);
        verify(todoRepository).findByStatusTrue();
    }

    //5 - Handle case with very large number of Todo items
    @Test
    public void test_get_all_with_large_number_of_todos() {

        List<Todo> largeTodoList = new ArrayList<>();
        for (long i = 0; i < 10000; i++) {
            largeTodoList.add(new Todo(i, "Task " + i, true, true));
        }

        when(todoRepository.findByStatusTrue()).thenReturn(largeTodoList);

        List<Todo> actualTodos = todoService.getAll();

        assertEquals(largeTodoList.size(), actualTodos.size());
        assertEquals(largeTodoList, actualTodos);
        verify(todoRepository).findByStatusTrue();
    }

    //----------Create----4 testcases------
    //1 - HappyCase - Successfully create a new Todo with valid title from TodoDTO
    @Test
    public void test_create_todo_with_valid_title() {

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("Test Todo");

        Todo expectedTodo = new Todo();
        expectedTodo.setTitle("Test Todo");
        expectedTodo.setCompleted(false);
        expectedTodo.setStatus(true);

        when(todoRepository.save(any(Todo.class))).thenReturn(expectedTodo);

        Todo result = todoService.createTodo(todoDTO);

        assertNotNull(result);
        assertEquals("Test Todo", result.getTitle());
        assertFalse(result.isCompleted());
        assertTrue(result.isStatus());
        verify(todoRepository).save(any(Todo.class));
    }

    //2 - Handle TodoDTO with null title
    @Test
    public void test_create_todo_with_null_title() {

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle(null);

        Todo expectedTodo = new Todo();
        expectedTodo.setTitle(null);
        expectedTodo.setCompleted(false);
        expectedTodo.setStatus(true);

        when(todoRepository.save(any(Todo.class))).thenReturn(expectedTodo);

        Todo result = todoService.createTodo(todoDTO);

        assertNotNull(result);
        assertNull(result.getTitle());
        assertFalse(result.isCompleted());
        assertTrue(result.isStatus());
        verify(todoRepository).save(any(Todo.class));
    }

    //3 - Handle case when TodoRepository.save() fails
    @Test
    public void test_create_todo_save_failure() {
        TodoRepository todoRepository = mock(TodoRepository.class);
        TodoService todoService = new TodoService();
        todoService.todoRepository = todoRepository;

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("Test Todo");

        when(todoRepository.save(any(Todo.class))).thenThrow(new RuntimeException("Save failed"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            todoService.createTodo(todoDTO);
        });

        assertEquals("Save failed", exception.getMessage());
        verify(todoRepository).save(any(Todo.class));
    }

    //4 - Verify Todo ID is generated automatically
    @Test
    public void test_todo_id_generated_automatically() {
        TodoRepository todoRepository = mock(TodoRepository.class);
        TodoService todoService = new TodoService();
        todoService.todoRepository = todoRepository;

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("Sample Todo");

        Todo savedTodo = new Todo();
        savedTodo.setId(1L); // Simulate ID generation
        savedTodo.setTitle("Sample Todo");
        savedTodo.setCompleted(false);
        savedTodo.setStatus(true);

        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        Todo result = todoService.createTodo(todoDTO);

        assertNotNull(result);
        assertNotNull(result.getId()); // Verify ID is generated
        assertEquals("Sample Todo", result.getTitle());
        assertFalse(result.isCompleted());
        assertTrue(result.isStatus());
        verify(todoRepository).save(any(Todo.class));
    }

    //----------Delete----3 testcases------
    //1 - HappyCase - Successfully soft delete todo by setting status to false when valid ID is provided
    @Test
    public void test_delete_todo_sets_status_false() {

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setStatus(true);

        when(todoRepository.findTodoById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        Todo result = todoService.deleteTodo(1L);

        assertFalse(result.isStatus());
        verify(todoRepository).save(todo);
    }

    //2 - Throw NotFoundException when todo with provided ID does not exist
    @Test
    public void test_delete_todo_throws_not_found() {

        when(todoRepository.findTodoById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            todoService.deleteTodo(1L);
        });

        verify(todoRepository, never()).save(any(Todo.class));
    }

    //3 - Handle case when repository findTodoById returns empty Optional
    @Test
    public void test_delete_todo_throws_not_found_exception() {

        when(todoRepository.findTodoById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            todoService.deleteTodo(1L);
        });

        verify(todoRepository, never()).save(any(Todo.class));
    }

//----------Update----3 testcases------
    //1 - HappyCase - Successfully update todo title when valid ID and todoDTO are provided
    @Test
    public void test_update_todo_title_success() {

        Todo existingTodo = new Todo();
        existingTodo.setId(1L);
        existingTodo.setTitle("Old Title");
        existingTodo.setCompleted(false);
        existingTodo.setStatus(true);

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("New Title");

        when(todoRepository.findTodoById(1L)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(existingTodo);

        Todo updatedTodo = todoService.updateTodoById(1L, todoDTO);

        verify(todoRepository).findTodoById(1L);
        verify(todoRepository).save(existingTodo);
        assertEquals("New Title", updatedTodo.getTitle());
    }

    //2 - Throw NotFoundException when todo with given ID does not exist
    @Test
    public void test_update_todo_not_found() {
        TodoRepository todoRepository = mock(TodoRepository.class);
        TodoService todoService = new TodoService();
        todoService.todoRepository = todoRepository;

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("New Title");

        when(todoRepository.findTodoById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            todoService.updateTodoById(1L, todoDTO);
        });

        verify(todoRepository).findTodoById(1L);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    //3 - Verify that only title field is updated
    @Test
    public void test_update_todo_title_only() {
        TodoRepository todoRepository = mock(TodoRepository.class);
        TodoService todoService = new TodoService();
        todoService.todoRepository = todoRepository;

        Todo existingTodo = new Todo();
        existingTodo.setId(1L);
        existingTodo.setTitle("Old Title");
        existingTodo.setCompleted(false);
        existingTodo.setStatus(true);

        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("New Title");

        when(todoRepository.findTodoById(1L)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(existingTodo);

        Todo updatedTodo = todoService.updateTodoById(1L, todoDTO);

        verify(todoRepository).findTodoById(1L);
        verify(todoRepository).save(existingTodo);
        assertEquals("New Title", updatedTodo.getTitle());
        assertFalse(updatedTodo.isCompleted());
        assertTrue(updatedTodo.isStatus());
    }

    //-------------Update Complete--------------
    // Toggle completed status from false to true for existing todo
    @Test
    public void test_toggle_completed_status_from_false_to_true() {

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setCompleted(false);

        when(todoRepository.findTodoById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // Act
        Todo result = todoService.updateTodoComplete(1L);

        // Assert
        assertTrue(result.isCompleted());
        verify(todoRepository).save(todo);
    }

    // Toggle completed status from true to false for existing todo
    @Test
    public void test_toggle_completed_status_from_true_to_false() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setCompleted(true);

        when(todoRepository.findTodoById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // Act
        Todo result = todoService.updateTodoComplete(1L);

        // Assert
        assertFalse(result.isCompleted());
        verify(todoRepository).save(todo);
    }

    // Handle non-existent todo ID by throwing NotFoundException
    @Test
    public void test_throw_not_found_exception_for_invalid_id() {

        when(todoRepository.findTodoById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            todoService.updateTodoComplete(999L);
        });
        verify(todoRepository, never()).save(any(Todo.class));
    }

    // Handle case when repository save operation fails
    @Test
    public void test_update_todo_complete_save_failure() {

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setCompleted(false);

        when(todoRepository.findTodoById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenThrow(new RuntimeException("Save operation failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            todoService.updateTodoComplete(1L);
        });
        verify(todoRepository).save(todo);
    }

    // Handle null ID parameter
    @Test
    public void test_update_todo_complete_with_null_id() {
        assertThrows(IllegalArgumentException.class, () -> {
            todoService.updateTodoComplete(null);
        });
    }

//    //Handle TodoDTO with title longer than 50 characters
//    @Test
//    public void test_create_todo_with_title_exceeding_50_characters() {
//
//        TodoDTO todoDTO = new TodoDTO();
//        todoDTO.setTitle("This title is definitely longer than fifty characters in length");
//
//        Set<ConstraintViolation<TodoDTO>> violations = validator.validate(todoDTO);
//        assertFalse(violations.isEmpty());
//
//        for (ConstraintViolation<TodoDTO> violation : violations) {
//            assertEquals("Title todo must not exceed 50 characters", violation.getMessage());
//        }
//
//        verify(todoRepository, never()).save(any(Todo.class));
//    }
//
//    //Handle TodoDTO with empty title
//    @Test
//    public void test_create_todo_with_empty_title() {
//
//        TodoDTO todoDTO = new TodoDTO();
//        todoDTO.setTitle("");
//
//        Set<ConstraintViolation<TodoDTO>> violations = validator.validate(todoDTO);
//        assertFalse(violations.isEmpty());
//
//        for (ConstraintViolation<TodoDTO> violation : violations) {
//            assertEquals("Title must not be blank", violation.getMessage());
//        }
//
//        verify(todoRepository, never()).save(any(Todo.class));
//    }
//
//    //Handle TodoDTO with special characters in title
//    @Test
//    public void test_create_todo_with_special_characters_in_title() {
//
//        TodoDTO todoDTO = new TodoDTO();
//        todoDTO.setTitle("Invalid@Title!");
//
//        Set<ConstraintViolation<TodoDTO>> violations = validator.validate(todoDTO);
//        assertFalse(violations.isEmpty());
//
//        for (ConstraintViolation<TodoDTO> violation : violations) {
//            assertEquals("Title todo must not contain special characters", violation.getMessage());
//        }
//
//        verify(todoRepository, never()).save(any(Todo.class));
//    }



}