package ru.practicum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.service.ParticipationRequestService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
public class PrivateController {

    private final EventService eventService;
    private final ParticipationRequestService participationRequestService;

    public PrivateController(EventService eventService,
                             ParticipationRequestService participationRequestService) {
        this.eventService = eventService;
        this.participationRequestService = participationRequestService;
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<Object> addEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto dto) {
        return new ResponseEntity<>(eventService.addEvent(dto, userId), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<Object> getAllEventsForInitiator(@PathVariable Long userId) {
        return new ResponseEntity<>(eventService.getAllEventWhereUserIsInitiator(userId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return new ResponseEntity<>(eventService.getEventOfCurrentUser(eventId, userId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                   @RequestBody UpdateEventUserRequest updateDto) {
        return new ResponseEntity<>(eventService.updateEventByUser(eventId, userId, updateDto), HttpStatus.OK);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<Object> addParticipationRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {
        return new ResponseEntity<>(participationRequestService.addParticipationRequest(userId, eventId),
                HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<Object> cancelParticipationRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return new ResponseEntity<>(participationRequestService.cancelParticipationRequest(userId, requestId),
                HttpStatus.OK);
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<Object> getAllRequestsByUser(@PathVariable Long userId) {
        return new ResponseEntity<>(participationRequestService.getAllRequestsByUser(userId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> getRequestsByUserForEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return new ResponseEntity<>(participationRequestService.getAllRequestsForEvent(userId, eventId),
                HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> changeRequestStatusByEventInitiator(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequestDto dto) {
        return new ResponseEntity<>(participationRequestService.updateRequestStatus(userId, eventId, dto),
                HttpStatus.OK);
    }
}
