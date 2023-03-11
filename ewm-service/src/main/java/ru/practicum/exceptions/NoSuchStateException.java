package ru.practicum.exceptions;

public class NoSuchStateException extends RuntimeException {
    public NoSuchStateException(String message) {
        super(message);
    }
}
