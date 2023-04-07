package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.validation.annotation.Validated;
import ru.practicum.location.dto.LocationDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Validated
public class NewEventDto {

    @Size(min = 20, max = 2000, message = "аннотация должна быть от 20 до 7000 символов")
    @NotNull(message = "аннотация должна быть от 20 до 7000 символов")
    private String annotation;
    @NotNull(message = "категория не должна быть пустой")
    private Long category;
    @Size(min = 20, max = 7000, message = "описание должно быть от 20 до 7000 символов")
    @NotNull(message = "описание должно быть от 20 до 7000 символов")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "дата события не должна быть пустой")
    private LocalDateTime eventDate;
    @NotNull(message = "локация не должна быть пустой")
    private LocationDto location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    @Size(min = 3, max = 120, message = "название должно быть от 3 до 120 символов")
    @NotNull(message = "название должно быть от 3 до 120 символов")
    private String title;

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Long getParticipantLimit() {
        return participantLimit;
    }

    public void setParticipantLimit(Long participantLimit) {
        this.participantLimit = participantLimit;
    }

    public Boolean getRequestModeration() {
        return requestModeration;
    }

    public void setRequestModeration(Boolean requestModeration) {
        this.requestModeration = requestModeration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
