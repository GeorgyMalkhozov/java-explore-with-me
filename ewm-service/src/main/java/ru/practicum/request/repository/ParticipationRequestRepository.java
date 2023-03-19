package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.dto.RequestCounter;
import ru.practicum.request.enums.ParticipationRequestStatus;
import ru.practicum.request.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterIdOrderByIdAsc(Long requesterId);
    List<ParticipationRequest> findAllByEventIdOrderByIdAsc(Long eventId);
    List<ParticipationRequest> findAllByIdIn(List<Long> requestIds);
    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventIds,
                                                          ParticipationRequestStatus participationRequestStatus);
    Long countAllByEventIdAndStatus(Long eventId, ParticipationRequestStatus participationRequestStatus);
    @Query(value = "SELECT new ru.practicum.request.dto.RequestCounter( P.event.id, COUNT(P.id) ) " +
            "FROM ParticipationRequest AS P " +
            "WHERE P.event.id in (?1) AND P.status = ?2 " +
            "GROUP BY P.event.id ")
    List<RequestCounter> countAllConfirmedForTheListOfEvents(List<Long> eventIds,
                                                       ParticipationRequestStatus participationRequestStatus);
}
