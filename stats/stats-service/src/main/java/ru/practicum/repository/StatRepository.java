package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Hit;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Hit, Integer> {

    @Query("SELECT new ru.practicum.model.Stats(hit.app, hit.uri, " +
            "(CASE WHEN (:unique = true) THEN count(distinct hit.ip) ELSE count(hit.ip) END)) " +
            "FROM Hit AS hit " +
            "WHERE hit.timestamp > :start AND hit.timestamp < :end AND hit.uri IN :uris " +
            "GROUP BY hit.app, hit.uri ")
    List<Stats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
