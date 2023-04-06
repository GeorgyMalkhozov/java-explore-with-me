package ru.practicum.request.service;

import ru.practicum.event.model.Event;
import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

 public interface ParticipationRequestService {
    
     ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);
    
     ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);

     List<ParticipationRequestDto> getAllRequestsByUser(Long userId);

     List<ParticipationRequestDto> getAllRequestsForEvent(Long userId, Long eventId);
    
     EventRequestStatusUpdateResultDto updateRequestStatus(Long userId, Long eventId,
                                                                 EventRequestStatusUpdateRequestDto dto);
     void setConfirmedRequestsCountToEvents(List<Event> events);
}
