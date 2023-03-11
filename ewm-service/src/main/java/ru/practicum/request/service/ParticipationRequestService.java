package ru.practicum.request.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.event.dao.EventDao;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.request.dao.ParticipationRequestDao;
import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.enums.ParticipationRequestStatus;
import ru.practicum.request.mapper.ParticipationRequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.repository.ParticipationRequestRepository;
import ru.practicum.user.dao.UserDao;
import ru.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserDao userDao;
    private final ParticipationRequestDao participationRequestDao;
    private final EventDao eventDao;

    public ParticipationRequestService(ParticipationRequestRepository participationRequestRepository, ParticipationRequestMapper participationRequestMapper, UserRepository userRepository, EventRepository eventRepository, UserDao userDao, ParticipationRequestDao participationRequestDao, EventDao eventDao) {
        this.participationRequestRepository = participationRequestRepository;
        this.participationRequestMapper = participationRequestMapper;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
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
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequestDao.enrichNewRequestWithData(participationRequest, event, userId);
        // проверка есть ли свободные места
        try {
            participationRequestRepository.save(participationRequest);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Заявка должна быть уникальной");
        }
        return participationRequestMapper.participationRequestToDto(participationRequest);
    }

    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        userDao.checkUserExist(userId);
        participationRequestDao.checkParticipationRequestExist(requestId);
        ParticipationRequest participationRequest = participationRequestDao.getRequestById(requestId);
        participationRequestDao.checkUserIsRequestor(userId, participationRequest);
        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);
        try {
            participationRequestRepository.save(participationRequest);
        } catch (TransactionSystemException e) {
            throw new ValidationException("поле не может быть пустыми");
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("заявка должна быть уникальной");
        }
        return participationRequestMapper.participationRequestToDto(participationRequest);
    }

    public List<ParticipationRequestDto> getAllRequestsByUser(Long userId) {
        userDao.checkUserExist(userId);
        List<ParticipationRequest> participationRequests = participationRequestRepository.
                findAllByRequesterIdOrderByIdAsc(userId);
        return participationRequests.stream()
                .map(participationRequestMapper::participationRequestToDto)
                .collect(Collectors.toList());
    }

    public List<ParticipationRequestDto> getAllRequestsByUserForEvent(Long userId, Long eventId) {
        userDao.checkUserExist(userId);
        eventDao.checkEventExist(eventId);
        List<ParticipationRequest> participationRequests = participationRequestRepository.
                findAllByRequesterIdAndEventIdOrderByIdAsc(userId,eventId);
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

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        // проверить что все реквесты относятся именно к этому ивенту

        switch (participationRequestStatus) {
            case CONFIRMED:
                for (ParticipationRequest request : requests) {
                    if (request.getStatus().equals(ParticipationRequestStatus.PENDING)) {
                    // проверка свободных мест
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
