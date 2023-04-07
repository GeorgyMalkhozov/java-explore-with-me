package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.StatsDTO;
import ru.practicum.model.Stats;

@Mapper(componentModel = "spring")
public interface StatsMapper {

   StatsDTO statsToStatsDTO(Stats stats);
}
