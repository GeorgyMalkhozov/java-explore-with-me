package ru.practicum.event.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.EventStateActionAdmin;
import ru.practicum.event.enums.EventStateActionUser;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NoObjectsFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.request.enums.ParticipationRequestStatus;
import ru.practicum.request.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class EventDao {

    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    @Autowired
    public EventDao(EventRepository eventRepository,
                    ParticipationRequestRepository participationRequestRepository) {
        this.eventRepository = eventRepository;
        this.participationRequestRepository = participationRequestRepository;
    }

    public void checkEventExist(Long eventId) {
        if (!eventRepository.findById(eventId).isPresent()) {
            throw new NoObjectsFoundException("Событие с id = " + eventId + " не существует");
        }
    }

    public Event getEventById(Long id) {
        checkEventExist(id);
        Event event = eventRepository.getById(id);
        event.setConfirmedRequests(participationRequestRepository.countAllByEventIdAndStatus(id,
                ParticipationRequestStatus.CONFIRMED));
        return event;
    }

    public void checkUserIsInitiator(Long userId, Event event) {
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NoObjectsFoundException("Только инициатор события может вносить изменения");
        }
    }

    public void checkEventTimeIsMoreThanHoursLaterAfterPublishTime(Event event, Integer numberOfHours) {
        if (LocalDateTime.now().plusHours(numberOfHours).isAfter(event.getEventDate())) {
            throw new ConflictException("Нельзя изменять событие: между началом события и " +
                    "датой публикации менее " + numberOfHours + " часа");
        }
    }

    public void checkEventStateReadyForConfirmOrDecline(Event event) {
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Нельзя опубликовать или отклонить событие в статусе: " + event.getState());
        }
    }

    public void checkEventStateIsPublished(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Для данного действия событие должно находится в статусе PUBLISHED");
        }
    }

    public void checkIfEventCanBeModifiedByState(Event event) {
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие в статусе PUBLISHED не может быть изменено");
        }
    }

    public void eventStateUserActionProcessing(Event event, String eventStateAction) {
        if (eventStateAction == null) { return;}
        event.setState(EventStateActionUser.convert(eventStateAction)
                .equals(EventStateActionUser.CANCEL_REVIEW) ? EventState.CANCELED : EventState.PENDING);
    }

    public void eventStateAdminActionProcessing(Event event, String eventStateAction) {
        if (eventStateAction == null) { return;}
        checkEventStateReadyForConfirmOrDecline(event);
        if (EventStateActionAdmin.convert(eventStateAction).equals(EventStateActionAdmin.PUBLISH_EVENT)) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else {
            event.setState(EventState.CANCELED);
        }
    }

    public void saveEvent(Event event) {
        try {
            eventRepository.saveAndFlush(event);
        } catch (TransactionSystemException e) {
            throw new ValidationException("поле name не может быть пустыми");
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("name должен быть уникальным");
        }
    }
}
