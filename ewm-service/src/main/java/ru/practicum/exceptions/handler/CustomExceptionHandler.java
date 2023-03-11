package ru.practicum.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NoObjectsFoundException;
import ru.practicum.exceptions.NoSuchStateException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.exceptions.model.ApiError;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({NoObjectsFoundException.class})
    protected ResponseEntity<Object> handleNotFound(Exception exception) {
        ApiError apiError = new ApiError(
                List.of(exception.getClass().getSimpleName()),
                exception.getMessage(),
                "Объект не обнаружен",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ValidationException.class})
    protected ResponseEntity<Object> handleNotAcceptable(Exception exception) {
        ApiError apiError = new ApiError(
                List.of(exception.getClass().getSimpleName()),
                exception.getMessage(),
                "Недопустимое действие",
                HttpStatus.NOT_ACCEPTABLE,
                LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler({NoSuchStateException.class})
    protected ResponseEntity<Object> handleBadRequest(Exception exception) {
        ApiError apiError = new ApiError(
                List.of(exception.getClass().getSimpleName()),
                exception.getMessage(),
                "Некорректный запрос",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConflictException.class})
    protected ResponseEntity<Object> handleConflict(Exception exception) {
        ApiError apiError = new ApiError(
                List.of(exception.getClass().getSimpleName()),
                exception.getMessage(),
                "Конфликт данных",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
}
