package ru.practicum.request.dto;

import java.util.List;

public class EventRequestStatusUpdateRequestDto {

    private List<Long> requestIds;
    private String status;

    public List<Long> getRequestIds() {
        return requestIds;
    }

    public void setRequestIds(List<Long> requestIds) {
        this.requestIds = requestIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
