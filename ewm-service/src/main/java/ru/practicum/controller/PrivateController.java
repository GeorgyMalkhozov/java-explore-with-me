package ru.practicum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/private")
public class PrivateController {

    private final CategoryService categoryService;
    private final EventService eventService;

    private final ParticipationRequestService participationRequestService;

    public PrivateController(CategoryService categoryService, EventService eventService,
                             ParticipationRequestService participationRequestService) {
        this.categoryService = categoryService;
        this.eventService = eventService;
        this.participationRequestService = participationRequestService;
    }

    @PostMapping("/users/{userId}/events")
    public ResponseEntity<Object> addEvent(@PathVariable Long userId, @RequestBody NewEventDto dto) {
        return new ResponseEntity<>(eventService.addEvent(dto, userId), HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/events")
    public ResponseEntity<Object> getAllEventsForInitiator(@PathVariable Long userId) {
        return new ResponseEntity<>(eventService.getAllEventWhereUserIsInitiator(userId), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<Object> getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return new ResponseEntity<>(eventService.getEventOfCurrentUser(eventId, userId), HttpStatus.OK);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<Object> updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                   @RequestBody UpdateEventUserRequest updateDto) {
        return new ResponseEntity<>(eventService.updateEventByUser(eventId, userId, updateDto), HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/requests")
    public ResponseEntity<Object> addParticipationRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {
        return new ResponseEntity<>(participationRequestService.addParticipationRequest(userId, eventId),
                HttpStatus.CREATED);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<Object> cancelParticipationRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return new ResponseEntity<>(participationRequestService.cancelParticipationRequest(userId, requestId),
                HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/requests")
    public ResponseEntity<Object> getAllRequestsByUser(@PathVariable Long userId) {
        return new ResponseEntity<>(participationRequestService.getAllRequestsByUser(userId), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> getRequestsByUserForEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return new ResponseEntity<>(participationRequestService.getAllRequestsByUserForEvent(userId, eventId),
                HttpStatus.OK);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> changeRequestStatusByEventInitiator(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequestDto dto) {
        return new ResponseEntity<>(participationRequestService.updateRequestStatus(userId, eventId, dto),
                HttpStatus.OK);
    }
}
