package ru.practicum.event.enums;

import ru.practicum.exceptions.NoSuchStateException;

public enum EventStateActionAdmin {

    PUBLISH_EVENT("PUBLISH_EVENT"),
    REJECT_EVENT("REJECT_EVENT");

    private final String name;

    EventStateActionAdmin(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EventStateActionAdmin convert(String stateAction) {
        try {
            return EventStateActionAdmin.valueOf(stateAction.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NoSuchStateException("Unknown state: " + stateAction);
        }
    }
}
