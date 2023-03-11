package ru.practicum.request.dao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NoObjectsFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.request.enums.ParticipationRequestStatus;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.repository.ParticipationRequestRepository;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class ParticipationRequestDao {

    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public ParticipationRequestDao(ParticipationRequestRepository participationRequestRepository,
                                   UserRepository userRepository, EventRepository eventRepository) {
        this.participationRequestRepository = participationRequestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
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

    public void checkUserIsNotEventInitiatorBeforeNewRequest(Long userId, Event event) {
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NoObjectsFoundException("Инициатор события не может быть участником");
        }
    }

    public void enrichNewRequestWithData(ParticipationRequest participationRequest, Event event, Long userId) {
        participationRequest.setRequester(userRepository.getById(userId));
        participationRequest.setEvent(event);
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setStatus(ParticipationRequestStatus.PENDING);
        if (!event.isRequestModeration()) {participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED); }
    }

    public void saveRequest(ParticipationRequest participationRequest) {
        try {
            participationRequestRepository.saveAndFlush(participationRequest);
        } catch (TransactionSystemException e) {
            throw new ValidationException("поле не может быть пустыми");
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("заявка должна быть уникальной");
        }
    }
}
