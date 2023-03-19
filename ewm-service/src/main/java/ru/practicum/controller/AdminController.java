package ru.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin")
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @Autowired
    public AdminController(UserService userService, CategoryService categoryService, EventService eventService,
                           CompilationService compilationService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.eventService = eventService;
        this.compilationService = compilationService;
    }

    @PostMapping("/users")
    public ResponseEntity<Object> addUser(@RequestBody @Validated NewUserRequest dto) {
        return new ResponseEntity<>(userService.addUser(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("Пользователь удален", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return new ResponseEntity<>(userService.getUsers(ids, from, size), HttpStatus.OK);
    }

    @PostMapping("/categories")
    public ResponseEntity<Object> addCategory(@RequestBody @Validated NewCategoryDto dto) {
        return new ResponseEntity<>(categoryService.addCategory(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
        return new ResponseEntity<>("Категория удалена", HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId, @RequestBody @Validated NewCategoryDto updateDto) {
        return categoryService.updateCategory(catId, updateDto);
    }

    @GetMapping("/events")
    public ResponseEntity<Object> getEventsByAdminWithFilter(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return new ResponseEntity<>(eventService.getEventsByAdminWithFilter(
                users, states, categories, rangeStart, rangeEnd, from, size), HttpStatus.OK);
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<Object> updateEvent(@PathVariable Long eventId,
                                              @RequestBody UpdateEventAdminRequest updateDto) {
        return new ResponseEntity<>(eventService.updateEventByAdmin(eventId, updateDto), HttpStatus.OK);
    }

    @PostMapping("/compilations")
    public ResponseEntity<Object> addCompilation(@RequestBody @Validated NewCompilationDto dto) {
        return new ResponseEntity<>(compilationService.addCompilation(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/compilations/{compId}")
    public ResponseEntity<Object> deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
        return new ResponseEntity<>("Подборка удалена", HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/compilations/{compId}")
    public ResponseEntity<Object> updateCompilation(@PathVariable Long compId,
                                              @RequestBody UpdateCompilationDto updateDto) {
        return new ResponseEntity<>(compilationService.updateCompilation(compId, updateDto), HttpStatus.OK);
    }
}
