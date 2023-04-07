package ru.practicum.compilation.dto;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Validated
public class NewCompilationDto {

    private Set<Long> events;
    private boolean pinned;
    @NotBlank(message = "название не должно быть пустым")
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
