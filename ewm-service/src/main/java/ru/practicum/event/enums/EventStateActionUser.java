package ru.practicum.event.enums;

import ru.practicum.exceptions.NoSuchStateException;

public enum EventStateActionUser {

    SEND_TO_REVIEW("SEND_TO_REVIEW"),
    CANCEL_REVIEW("CANCEL_REVIEW");

    private final String name;

    EventStateActionUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EventStateActionUser convert(String stateAction) {
        try {
            return EventStateActionUser.valueOf(stateAction.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NoSuchStateException("Unknown state: " + stateAction);
        }
    }
}
