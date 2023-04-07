package ru.practicum.compilation.dto;

import org.springframework.validation.annotation.Validated;
import ru.practicum.event.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Validated
public class CompilationDto {

    private Set<EventShortDto> events;
    private Long id;
    private boolean pinned;
    @NotBlank(message = "Название компиляции не должно быть пустым")
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
