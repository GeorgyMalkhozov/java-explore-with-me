package ru.practicum.comments.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NewCommentDto {

    @Size(min = 5, max = 10000, message = "описание должно быть от 20 до 10000 символов")
    @NotNull(message = "описание должно быть от 5 до 10000 символов")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
