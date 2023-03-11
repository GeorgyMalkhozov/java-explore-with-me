package ru.practicum.compilation.dto;

import org.springframework.validation.annotation.Validated;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Set;

@Validated
public class CompilationDto {

    private Set<EventShortDto> events;
    private Long id;
    private boolean pinned;
    private String title;

    public Set<EventShortDto> getEvents() {
        return events;
    }

    public void setEvents(Set<EventShortDto> events) {
        this.events = events;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
