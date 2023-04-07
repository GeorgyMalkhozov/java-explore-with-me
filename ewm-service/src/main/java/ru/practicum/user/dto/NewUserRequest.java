package ru.practicum.user.dto;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Validated
public class NewUserRequest {

    @NotBlank(message = "имя не должно быть пустым")
    private String name;
    @Email(message = "электронная почта не соответствует формату")
    @NotBlank(message = "электронная почта не должна быть пустой")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
