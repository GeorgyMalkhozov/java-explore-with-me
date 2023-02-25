package ru.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatService;
import ru.practicum.dto.HitDTO;
import ru.practicum.dto.StatsDTO;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class StatController {

private final StatService statService;

    @Autowired
    public StatController(StatService statService) {
        this.statService = statService;
    }

    @PostMapping("/hit")
    public HitDTO addStats(@RequestBody HitDTO dto) {
        return statService.addStats(dto);
    }

    @GetMapping("/stats")
    public List<StatsDTO> getStats(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false) boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }
}
