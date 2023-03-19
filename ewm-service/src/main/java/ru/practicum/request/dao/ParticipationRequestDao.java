package ru.practicum.request.dao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NoObjectsFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.request.dto.RequestCounter;
import ru.practicum.request.enums.ParticipationRequestStatus;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.repository.ParticipationRequestRepository;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ParticipationRequestDao {

    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;

    public ParticipationRequestDao(ParticipationRequestRepository participationRequestRepository,
                                   UserRepository userRepository) {
        this.participationRequestRepository = participationRequestRepository;
        this.userRepository = userRepository;
    }

    public void checkParticipationRequestExist(Long requestId) {
        if (!participationRequestRepository.findById(requestId).isPresent()) {
            throw new NoObjectsFoundException("Заявка на участие с id = " + requestId + " не существует");
        }
    }

    public ParticipationRequest getRequestById(Long requestId) {
        checkParticipationRequestExist(requestId);
        return participationRequestRepository.getById(requestId);
    }

    public void checkUserIsRequestor(Long userId, ParticipationRequest request) {
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new NoObjectsFoundException("Пользователь не является инициатором заявки");
        }
    }

    public void checkRequestStatusIsAvailableToCancel(ParticipationRequest request) {
        if (!Objects.equals(request.getStatus(), ParticipationRequestStatus.PENDING)) {
            throw new ConflictException("Может быть отменена только заявка в статусе PENDING");
        }
    }

    public void checkUserIsNotEventInitiatorBeforeNewRequest(Long userId, Event event) {
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Инициатор события не может быть участником");
        }
    }

    public void checkEventIsNotFullyBooked(Event event) {
        Long bookedSpaces = participationRequestRepository.countAllByEventIdAndStatus(event.getId(),
                ParticipationRequestStatus.CONFIRMED);
        if (event.getParticipantLimit().equals(0L)) {
            return;
        }
        if (Objects.equals(bookedSpaces, event.getParticipantLimit())) {
            massCancelRequestsWhenEventIsFull(event.getId());
            throw new ConflictException("Не осталось свободных мест для участия в событии");
        }
    }

    public void enrichNewRequestWithData(ParticipationRequest participationRequest, Event event, Long userId) {
        participationRequest.setRequester(userRepository.getById(userId));
        participationRequest.setEvent(event);
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setStatus(ParticipationRequestStatus.PENDING);
        if (Boolean.FALSE.equals(event.getRequestModeration())) {
            participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
        }
    }

    public void saveRequest(ParticipationRequest participationRequest) {
        try {
            participationRequestRepository.saveAndFlush(participationRequest);
        } catch (TransactionSystemException e) {
            throw new ValidationException("поле не может быть пустым");
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Пользователь не может отправлять более одной заявки на участие в событии");
        }
    }

    public void setConfirmedRequestsCountToEvents(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<RequestCounter> numberOfRequests = participationRequestRepository
                .countAllConfirmedForTheListOfEvents(eventIds, ParticipationRequestStatus.CONFIRMED);
        for (Event event : events) {
            for (RequestCounter numberOfRequestList : numberOfRequests) {
                if (event.getId().equals(numberOfRequestList.getEventId())) {
                    event.setConfirmedRequests(numberOfRequestList.getConfirmedRequests());
                }
            }
        }
    }

    public void massCancelRequestsWhenEventIsFull(Long eventId) {
        List<ParticipationRequest> participationRequests =
                participationRequestRepository.findAllByEventIdAndStatus(eventId, ParticipationRequestStatus.PENDING);
        participationRequests.forEach(participationRequest ->
                participationRequest.setStatus(ParticipationRequestStatus.REJECTED));
        participationRequestRepository.saveAllAndFlush(participationRequests);
    }
}
