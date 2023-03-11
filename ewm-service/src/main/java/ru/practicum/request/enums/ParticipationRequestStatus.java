package ru.practicum.request.enums;

import ru.practicum.exceptions.NoSuchStateException;

public enum ParticipationRequestStatus {

    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    REJECTED("REJECTED"),
    CANCELED("CANCELED");

    private final String name;

    ParticipationRequestStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ParticipationRequestStatus convert(String status) {
        try {
            return ParticipationRequestStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new NoSuchStateException("Unknown status: " + status);
        }
    }
}
