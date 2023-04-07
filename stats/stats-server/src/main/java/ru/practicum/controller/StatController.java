package ru.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDTO;
import ru.practicum.service.StatService;

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
    public ResponseEntity<Object> addStats(@RequestBody HitDTO dto) {
        return new ResponseEntity<>(statService.addStats(dto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false) boolean unique) {
        return new ResponseEntity<>(statService.getStats(start, end, uris, unique), HttpStatus.OK);
    }
}
