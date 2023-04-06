package ru.practicum.request.dao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NoObjectsFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.request.enums.ParticipationRequestStatus;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.repository.ParticipationRequestRepository;

import java.util.Objects;

@Component
public class ParticipationRequestDao {

    private final ParticipationRequestRepository participationRequestRepository;

    public ParticipationRequestDao(ParticipationRequestRepository participationRequestRepository) {
        this.participationRequestRepository = participationRequestRepository;
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

    public ParticipationRequest getRequestById(Long id) {
        return participationRequestRepository.findById(id).orElseThrow(() ->
                new NoObjectsFoundException("Заявка на участие с id = " + id + " не существует"));
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
}
