package ru.practicum.exceptions;

public class NoObjectsFoundException extends RuntimeException {
    public NoObjectsFoundException(String message) {
        super(message);
    }

}
