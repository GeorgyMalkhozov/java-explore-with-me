package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.HitMapper;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Hit;
import ru.practicum.model.Stats;
import ru.practicum.repository.StatRepository;
import ru.practicum.dto.HitDTO;
import ru.practicum.dto.StatsDTO;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatService {

    private final StatRepository statsRepository;
    private final HitMapper hitMapper;
    private final StatsMapper statsMapper;

    @Autowired
    public StatService(StatRepository statsRepository, HitMapper hitMapper, StatsMapper statsMapper) {
        this.statsRepository = statsRepository;
        this.hitMapper = hitMapper;
        this.statsMapper = statsMapper;
    }

    public HitDTO addStats(HitDTO dto) {
        Hit hit = hitMapper.hitDTOToHit(dto);
        statsRepository.save(hit);
        return hitMapper.hitToHitDTO(hit);
    }

    public List<StatsDTO> getStats(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique) {
         return statsRepository.getStats(start, end, uri, unique).stream()
                 .sorted(Comparator.comparing(Stats::getHits))
                 .map(statsMapper::statsToStatsDTO)
                 .collect(Collectors.toList());
    }
}