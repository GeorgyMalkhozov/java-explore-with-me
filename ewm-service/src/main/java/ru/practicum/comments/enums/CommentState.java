package ru.practicum.comments.enums;

import ru.practicum.exceptions.NoSuchStateException;

public enum CommentState {

    PUBLISHED("PUBLISHED"),
    BLOCKED("BLOCKED");

    private final String name;

    CommentState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CommentState convert(String state) {
        try {
            return CommentState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NoSuchStateException("Unknown state: " + state);
        }
    }
}
