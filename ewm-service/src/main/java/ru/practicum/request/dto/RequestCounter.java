package ru.practicum.request.dto;

public class RequestCounter {

    private Long eventId;
    private Long confirmedRequests;

    public RequestCounter(Long eventId, Long confirmedRequests) {
        this.eventId = eventId;
        this.confirmedRequests = confirmedRequests;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getConfirmedRequests() {
        return confirmedRequests;
    }

    public void setConfirmedRequests(Long confirmedRequests) {
        this.confirmedRequests = confirmedRequests;
    }

    @Override
    public String toString() {
        return "RequestCounter{" +
                "eventId=" + eventId +
                ", confirmedRequests=" + confirmedRequests +
                '}';
    }
}
