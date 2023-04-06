package ru.practicum.event.service;

import ru.practicum.event.dto.*;

import java.time.LocalDateTime;
import java.util.List;

 public interface EventService {

     EventFullDto addEvent(NewEventDto dto, Long userId);

     EventFullDto getPublishedEvent(Long id);

     List<EventFullDto> getAllPublishedEventsByFilter(
            String text, List<Integer> categories, Boolean paid, LocalDateTime start, LocalDateTime end,
            Boolean onlyAvailable, String sort, Integer from, Integer size);

     List<EventFullDto> getEventsByAdminWithFilter(
            List<Long> users, List<String> states,
            List<Long> categories, LocalDateTime start,
            LocalDateTime end, Integer from, Integer size);

     EventFullDto getEventOfCurrentUser(Long eventId, Long userId);

     List<EventShortDto> getAllEventWhereUserIsInitiator(Long userId);

     EventFullDto updateEventByUser(Long eventId, Long userId, UpdateEventUserRequest dto);

     EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);
}
