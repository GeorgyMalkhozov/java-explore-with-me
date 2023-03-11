package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoObjectsFoundException extends RuntimeException {
    public NoObjectsFoundException(String message) {
        super(message);
    }

}
