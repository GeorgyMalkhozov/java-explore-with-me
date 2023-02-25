package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.model.Stats;
import ru.practicum.dto.StatsDTO;

@Component
public class StatsMapper {

   public StatsDTO statsToStatsDTO(Stats stats) {
       return StatsDTO.builder()
               .app(stats.getApp())
               .uri(stats.getUri())
               .hits(stats.getHits())
               .build();
   }
}
