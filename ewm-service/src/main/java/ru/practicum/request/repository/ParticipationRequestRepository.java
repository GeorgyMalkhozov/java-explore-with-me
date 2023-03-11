package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterIdOrderByIdAsc(Long requesterId);
    List<ParticipationRequest> findAllByRequesterIdAndEventIdOrderByIdAsc(Long requesterId, Long eventId);
    List<ParticipationRequest> findAllByIdIn(List<Long> requestIds);
}
