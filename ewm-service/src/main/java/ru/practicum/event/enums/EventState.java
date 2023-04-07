package ru.practicum.event.enums;

import ru.practicum.exceptions.NoSuchStateException;

public enum EventState {

    PENDING("PENDING"),
    PUBLISHED("PUBLISHED"),
    CANCELED("CANCELED");

    private final String name;

    EventState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EventState convert(String state) {
        try {
            return EventState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NoSuchStateException("Unknown state: " + state);
        }
    }
}
