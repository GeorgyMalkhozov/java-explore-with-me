package ru.practicum.event.enums;

import ru.practicum.exceptions.NoSuchStateException;

public enum EventSortType {

    EVENT_DATE("EVENT_DATE"),
    VIEWS("VIEWS");

    private final String name;

    EventSortType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EventSortType convert(String state) {
        try {
            return EventSortType.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NoSuchStateException("Unknown sort type: " + state);
        }
    }
}
