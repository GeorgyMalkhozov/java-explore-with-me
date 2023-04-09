package ru.practicum.category.dto;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
public class NewCategoryDto {

    @NotBlank(message = "название не должно быть пустым")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}