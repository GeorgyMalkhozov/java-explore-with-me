package ru.practicum.request.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dao.EventDao;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.request.dao.ParticipationRequestDao;
import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestCounter;
import ru.practicum.request.enums.ParticipationRequestStatus;
import ru.practicum.request.mapper.ParticipationRequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.repository.ParticipationRequestRepository;
import ru.practicum.user.dao.UserDao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final UserDao userDao;
    private final ParticipationRequestDao participationRequestDao;
    private final EventDao eventDao;

    public ParticipationRequestServiceImpl(ParticipationRequestRepository participationRequestRepository,
                                           ParticipationRequestMapper participationRequestMapper, UserDao userDao,
                                           ParticipationRequestDao participationRequestDao, EventDao eventDao) {
        this.participationRequestRepository = participationRequestRepository;
        this.participationRequestMapper = participationRequestMapper;
        this.userDao = userDao;
        this.participationRequestDao = participationRequestDao;
        this.eventDao = eventDao;
    }

    @Transactional
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {

        userDao.getUserById(userId);
        Event event = eventDao.getEventById(eventId);
        eventDao.checkEventStateIsPublished(event);
        participationRequestDao.checkUserIsNotEventInitiatorBeforeNewRequest(userId, event);
        checkEventIsNotFullyBooked(event);
        ParticipationRequest participationRequest = new ParticipationRequest();
        enrichNewRequestWithData(participationRequest, event, userId);
        participationRequestDao.saveRequest(participationRequest);
        return participationRequestMapper.participationRequestToDto(participationRequest);
    }

    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {

        userDao.getUserById(userId);
        ParticipationRequest participationRequest = participationRequestDao.getRequestById(requestId);
        participationRequestDao.checkUserIsRequestor(userId, participationRequest);
        participationRequestDao.checkRequestStatusIsAvailableToCancel(participationRequest);
        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);
        participationRequestDao.saveRequest(participationRequest);
        return participationRequestMapper.participationRequestToDto(participationRequest);
    }

    public List<ParticipationRequestDto> getAllRequestsByUser(Long userId) {

        userDao.getUserById(userId);
        List<ParticipationRequest> participationRequests = participationRequestRepository
                .findAllByRequesterIdOrderByIdAsc(userId);
        return participationRequests.stream()
                .map(participationRequestMapper::participationRequestToDto)
                .collect(Collectors.toList());
    }

    public List<ParticipationRequestDto> getAllRequestsForEvent(Long userId, Long eventId) {

        userDao.getUserById(userId);
        eventDao.checkUserIsInitiator(userId, eventDao.getEventById(eventId));
        List<ParticipationRequest> participationRequests = participationRequestRepository
                .findAllByEventIdOrderByIdAsc(eventId);
        return participationRequests.stream()
                .map(participationRequestMapper::participationRequestToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResultDto updateRequestStatus(Long userId, Long eventId,
                                                                 EventRequestStatusUpdateRequestDto dto) {

        userDao.getUserById(userId);
        Event event = eventDao.getEventById(eventId);
        eventDao.checkUserIsInitiator(userId, event);
        eventDao.checkEventStateIsPublished(event);
        ParticipationRequestStatus participationRequestStatus = ParticipationRequestStatus.convert(dto.getStatus());
        List<ParticipationRequest> requests = participationRequestRepository.findAllByIdIn(dto.getRequestIds());
        requests.stream().filter(request -> !request.getStatus().equals(ParticipationRequestStatus.PENDING))
                .forEach(request -> {
            throw new ConflictException("Заявка № " + request.getId() + " не находится в статусе PENDING. " +
                    "Невозможно принять или отклонить эту заявку.");
        });
        return processRequestStatusUpdates(event,
                participationRequestStatus, requests);
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

    private EventRequestStatusUpdateResultDto processRequestStatusUpdates(
            Event event, ParticipationRequestStatus participationRequestStatus,
            List<ParticipationRequest> requests) {

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        List<ParticipationRequest> listForSave = new ArrayList<>();


        switch (participationRequestStatus) {
            case CONFIRMED:
                if (event.getParticipantLimit() != 0 &&
                        event.getParticipantLimit() - event.getConfirmedRequests() < requests.size()) {
                    throw new ConflictException("Недостаточно свободных мест для всех участников в списке");
                }
                for (ParticipationRequest request : requests) {
                    if (request.getStatus().equals(ParticipationRequestStatus.PENDING)) {
                        request.setStatus(ParticipationRequestStatus.CONFIRMED);
                        listForSave.add(request);
                        confirmedRequests.add(participationRequestMapper.participationRequestToDto(request));
                    }
                }
                break;
            case REJECTED:
                for (ParticipationRequest request : requests) {
                    if (request.getStatus().equals(ParticipationRequestStatus.PENDING)) {
                        request.setStatus(ParticipationRequestStatus.REJECTED);
                        listForSave.add(request);
                        rejectedRequests.add(participationRequestMapper.participationRequestToDto(request));
                    }
                }
                break;
            default:
                break;
        }
        participationRequestRepository.saveAllAndFlush(listForSave);
        EventRequestStatusUpdateResultDto eventRequestStatusUpdateResultDto = new EventRequestStatusUpdateResultDto();
        eventRequestStatusUpdateResultDto.setConfirmedRequests(confirmedRequests);
        eventRequestStatusUpdateResultDto.setRejectedRequests(rejectedRequests);
        return eventRequestStatusUpdateResultDto;
    }

    private void checkEventIsNotFullyBooked(Event event) {
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

    private void massCancelRequestsWhenEventIsFull(Long eventId) {
        List<ParticipationRequest> participationRequests =
                participationRequestRepository.findAllByEventIdAndStatus(eventId, ParticipationRequestStatus.PENDING);
        participationRequests.forEach(participationRequest ->
                participationRequest.setStatus(ParticipationRequestStatus.REJECTED));
        participationRequestRepository.saveAllAndFlush(participationRequests);
    }

    private void enrichNewRequestWithData(ParticipationRequest participationRequest, Event event, Long userId) {
        participationRequest.setRequester(userDao.getUserById(userId));
        participationRequest.setEvent(event);
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setStatus(ParticipationRequestStatus.PENDING);
        if (Boolean.FALSE.equals(event.getRequestModeration())) {
            participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
        }
    }
}
