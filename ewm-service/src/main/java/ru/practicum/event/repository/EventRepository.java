package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorIdOrderByIdAsc(Long initiatorId);

    List<Event> findAllByIdIn(List<Long> eventIds);

    List<Event> findAllByCategoryId(Long categoryId);
}
