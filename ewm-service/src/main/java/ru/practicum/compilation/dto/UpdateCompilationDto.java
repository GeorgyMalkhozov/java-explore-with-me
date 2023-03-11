package ru.practicum.compilation.dto;

import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Validated
public class UpdateCompilationDto {

    private Set<Long> events;
    private boolean pinned;
    private String title;

    public Set<Long> getEvents() {
        return events;
    }

    public void setEvents(Set<Long> events) {
        this.events = events;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
