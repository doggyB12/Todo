package com.project.Todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoDTO {
    @NotBlank(message = "Title must not be blank")
    @Size(max = 50, message = "Title todo must not exceed 50 characters")
    @Pattern(regexp = "^[0-9A-Za-z\\s.,]*$", message = "Title todo must not contain special characters")
    private String title;


}
