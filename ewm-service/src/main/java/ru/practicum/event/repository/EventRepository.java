package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.ParticipationRequest;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorIdOrderByIdAsc(Long initiatorId);

    List<Event> findAllByStateIs(EventState state);

    List<Event> findAllByIdIn(List<Long> eventIds);
}
