package ru.practicum.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.service.EventService;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/public")
public class PublicController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;

    public PublicController(UserService userService, CategoryService categoryService, EventService eventService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.eventService = eventService;
    }

    @GetMapping("/categories")
    public ResponseEntity<Object> getAllCategories(
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return new ResponseEntity<>(categoryService.getAllCategories(from, size), HttpStatus.OK);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getUser(@PathVariable Long catId) {
        return categoryService.getCategory(catId);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<Object> getPublishedEvent(@PathVariable Long eventId) {
        return new ResponseEntity<>(eventService.getPublishedEvent(eventId), HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<Object> getPublishedEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
            ) {
        return new ResponseEntity<>(eventService.getAllPublishedEventsByFilter(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size), HttpStatus.OK);
    }
}
