package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.request.enums.ParticipationRequestStatus;

import java.time.LocalDateTime;

public class ParticipationRequestDto {

    private Long id;
    private Long event;
    private Long requester;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private ParticipationRequestStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEvent() {
        return event;
    }

    public void setEvent(Long event) {
        this.event = event;
    }

    public Long getRequester() {
        return requester;
    }

    public void setRequester(Long requester) {
        this.requester = requester;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public ParticipationRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ParticipationRequestStatus status) {
        this.status = status;
    }
}
