package ru.practicum.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.event.dao.EventDao;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.request.dao.ParticipationRequestDao;
import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.enums.ParticipationRequestStatus;
import ru.practicum.request.mapper.ParticipationRequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.repository.ParticipationRequestRepository;
import ru.practicum.user.dao.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final UserDao userDao;
    private final ParticipationRequestDao participationRequestDao;
    private final EventDao eventDao;

    public ParticipationRequestService(ParticipationRequestRepository participationRequestRepository,
                                       ParticipationRequestMapper participationRequestMapper, UserDao userDao,
                                       ParticipationRequestDao participationRequestDao, EventDao eventDao) {
        this.participationRequestRepository = participationRequestRepository;
        this.participationRequestMapper = participationRequestMapper;
        this.userDao = userDao;
        this.participationRequestDao = participationRequestDao;
        this.eventDao = eventDao;
    }

    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {

        userDao.checkUserExist(userId);
        eventDao.checkEventExist(eventId);
        Event event = eventDao.getEventById(eventId);
        eventDao.checkEventStateIsPublished(event);
        participationRequestDao.checkUserIsNotEventInitiatorBeforeNewRequest(userId, event);
        participationRequestDao.checkEventIsNotFullyBooked(event);
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequestDao.enrichNewRequestWithData(participationRequest, event, userId);
        participationRequestDao.saveRequest(participationRequest);
        return participationRequestMapper.participationRequestToDto(participationRequest);
    }

    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {

        userDao.checkUserExist(userId);
        participationRequestDao.checkParticipationRequestExist(requestId);
        ParticipationRequest participationRequest = participationRequestDao.getRequestById(requestId);
        participationRequestDao.checkUserIsRequestor(userId, participationRequest);
        participationRequestDao.checkRequestStatusIsAvailableToCancel(participationRequest);
        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);
        participationRequestDao.saveRequest(participationRequest);
        return participationRequestMapper.participationRequestToDto(participationRequest);
    }

    public List<ParticipationRequestDto> getAllRequestsByUser(Long userId) {

        userDao.checkUserExist(userId);
        List<ParticipationRequest> participationRequests = participationRequestRepository
                .findAllByRequesterIdOrderByIdAsc(userId);
        return participationRequests.stream()
                .map(participationRequestMapper::participationRequestToDto)
                .collect(Collectors.toList());
    }

    public List<ParticipationRequestDto> getAllRequestsForEvent(Long userId, Long eventId) {

        userDao.checkUserExist(userId);
        eventDao.checkUserIsInitiator(userId, eventDao.getEventById(eventId));
        List<ParticipationRequest> participationRequests = participationRequestRepository
                .findAllByEventIdOrderByIdAsc(eventId);
        return participationRequests.stream()
                .map(participationRequestMapper::participationRequestToDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResultDto updateRequestStatus(Long userId, Long eventId,
                                                                 EventRequestStatusUpdateRequestDto dto) {

        userDao.checkUserExist(userId);
        eventDao.checkEventExist(eventId);
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

    private EventRequestStatusUpdateResultDto processRequestStatusUpdates(
            Event event, ParticipationRequestStatus participationRequestStatus,
            List<ParticipationRequest> requests) {

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        switch (participationRequestStatus) {
            case CONFIRMED:
                for (ParticipationRequest request : requests) {
                    if (request.getStatus().equals(ParticipationRequestStatus.PENDING)) {
                        participationRequestDao.checkEventIsNotFullyBooked(event);
                        request.setStatus(ParticipationRequestStatus.CONFIRMED);
                        participationRequestDao.saveRequest(request);
                        confirmedRequests.add(participationRequestMapper.participationRequestToDto(request));
                    }
                }
                break;
            case REJECTED:
                for (ParticipationRequest request : requests) {
                    if (request.getStatus().equals(ParticipationRequestStatus.PENDING)) {
                        request.setStatus(ParticipationRequestStatus.REJECTED);
                        participationRequestDao.saveRequest(request);
                        rejectedRequests.add(participationRequestMapper.participationRequestToDto(request));
                    }
                }
                break;
            default:
                break;
        }
        EventRequestStatusUpdateResultDto eventRequestStatusUpdateResultDto = new EventRequestStatusUpdateResultDto();
        eventRequestStatusUpdateResultDto.setConfirmedRequests(confirmedRequests);
        eventRequestStatusUpdateResultDto.setRejectedRequests(rejectedRequests);
        return eventRequestStatusUpdateResultDto;
    }
}
