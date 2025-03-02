package com.project.Todo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class TodoDTOTest {
    // Valid title with alphanumeric characters and spaces
    @Test
    public void test_valid_title_with_alphanumeric_chars() {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("My Todo 123");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TodoDTO>> violations = validator.validate(todoDTO);

        assertTrue(violations.isEmpty());
    }
    // Valid title with dots and commas
    @Test
    public void test_valid_title_with_dots_and_commas() {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("My Todo, with dots.");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TodoDTO>> violations = validator.validate(todoDTO);

        assertTrue(violations.isEmpty());
    }

    // Title with exactly 50 characters
    @Test
    public void test_title_with_exactly_50_characters() {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("12345678901234567890123456789012345678901234567890");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TodoDTO>> violations = validator.validate(todoDTO);

        assertTrue(violations.isEmpty());
    }

    // Empty title string
    @Test
    public void test_empty_title_validation() {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TodoDTO>> violations = validator.validate(todoDTO);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Title must not be blank", violations.iterator().next().getMessage());
    }

    // Title exceeding 50 characters
    @Test
    public void test_title_exceeding_50_characters() {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("This title is definitely going to exceed the fifty character limit");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TodoDTO>> violations = validator.validate(todoDTO);

        assertFalse(violations.isEmpty());
        assertEquals("Title todo must not exceed 50 characters", violations.iterator().next().getMessage());
    }

    // Title with special characters like !@#$%
    @Test
    public void test_title_with_special_characters() {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("Invalid!@#$%");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TodoDTO>> violations = validator.validate(todoDTO);

        assertFalse(violations.isEmpty());
        assertEquals("Title todo must not contain special characters", violations.iterator().next().getMessage());
    }

    // Title with only spaces
    @Test
    public void test_title_with_only_spaces() {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("     ");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TodoDTO>> violations = validator.validate(todoDTO);

        assertFalse(violations.isEmpty());
        assertEquals("Title must not be blank", violations.iterator().next().getMessage());
    }

    // Title with only punctuation
    @Test
    public void test_title_with_only_punctuation() {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("...,,,");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TodoDTO>> violations = validator.validate(todoDTO);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Title todo must not contain special characters", violations.iterator().next().getMessage());
    }
}
