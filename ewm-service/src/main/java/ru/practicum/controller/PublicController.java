package ru.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.category.service.CategoryServiceImpl;
import ru.practicum.client.StatClient;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.compilation.service.CompilationServiceImpl;
import ru.practicum.event.service.EventService;
import ru.practicum.event.service.EventServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
public class PublicController {

    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;
    private final StatClient statClient;

    @Autowired
    public PublicController(CategoryServiceImpl categoryService, EventServiceImpl eventService,
                            CompilationServiceImpl compilationService,
                            StatClient statClient
                            ) {
        this.categoryService = categoryService;
        this.eventService = eventService;
        this.compilationService = compilationService;
        this.statClient = statClient;
    }

    @GetMapping("/categories")
    public ResponseEntity<Object> getAllCategories(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return new ResponseEntity<>(categoryService.getAllCategories(from, size), HttpStatus.OK);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getUser(@PathVariable Long catId) {
        return categoryService.getCategory(catId);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<Object> getPublishedEvent(@PathVariable Long eventId, HttpServletRequest httpServletRequest) {
        statClient.addHit(httpServletRequest);
        return new ResponseEntity<>(eventService.getPublishedEvent(eventId), HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<Object> getPublishedEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest httpServletRequest
            ) {
        statClient.addHit(httpServletRequest);
        return new ResponseEntity<>(eventService.getAllPublishedEventsByFilter(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size), HttpStatus.OK);
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<Object> getCompilation(@PathVariable Long compId) {
        return new ResponseEntity<>(compilationService.getCompilation(compId), HttpStatus.OK);
    }

    @GetMapping("/compilations")
    public ResponseEntity<Object> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return new ResponseEntity<>(compilationService.getCompilationsByFilter(pinned, from, size), HttpStatus.OK);
    }
}
