package ru.practicum.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.category.dao.CategoryDao;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dao.EventDao;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.user.dao.UserDao;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventDao eventDao;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final CategoryDao categoryDao;
    private final UserDao userDao;


    @Autowired
    public EventService(EventRepository eventRepository, EventMapper eventMapper,
                        EventDao eventDao, CategoryRepository categoryRepository, UserRepository userRepository,
                        LocationRepository locationRepository, LocationMapper locationMapper, CategoryDao categoryDao, UserDao userDao) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.eventDao = eventDao;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.categoryDao = categoryDao;
        this.userDao = userDao;
    }

    public EventFullDto addEvent(NewEventDto dto, Long userId) {
        Event event = eventMapper.newEventDtoToEvent(dto);
        enrichNewEventWithData(dto, userId, event);
        try {
            eventRepository.save(event);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("должен быть уникальными"); ///!!!!!
        }
        return eventMapper.eventToEventFullDto(event);
    }

    public EventFullDto getPublishedEvent(Long id) {
        eventDao.checkEventExist(id);
        return eventMapper.eventToEventFullDto(eventDao.getEventById(id));
    }

    public List<EventFullDto> getAllPublishedEventsByFilter(
            String text, List<Integer> categories, boolean paid, LocalDateTime start, LocalDateTime end,
            boolean onlyAvailable, String sort, Integer from, Integer size) {
        // доработать
        List<Event> events = eventRepository.findAllByStateIs(EventState.PENDING); /// поменять на PUBLISHED
        return events.stream()
                .map(eventMapper::eventToEventFullDto)
                .collect(Collectors.toList());
    }

    public List<EventFullDto> getEventsByAdminWithFilter(
            List<Integer> users, List<String> states, List<Integer> categories, LocalDateTime start, LocalDateTime end,
            Integer from, Integer size) {
        // доработать
        List<Event> events = eventRepository.findAll(); /// поменять на PUBLISHED
        return events.stream()
                .map(eventMapper::eventToEventFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto getEventOfCurrentUser(Long eventId, Long userId) {
        userDao.checkUserExist(userId);
        eventDao.checkEventExist(eventId);
        Event event = eventDao.getEventById(eventId);
        eventDao.checkUserIsInitiator(userId, event);
        return eventMapper.eventToEventFullDto(event);
    }

    public List<EventShortDto> getAllEventWhereUserIsInitiator(Long userId) {
        userDao.checkUserExist(userId);
        List<Event> events = eventRepository.findAllByInitiatorIdOrderByIdAsc(userId);
        return events.stream()
                .map(eventMapper::eventToEventShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto updateEventByUser(Long eventId, Long userId, UpdateEventUserRequest dto) {
        userDao.checkUserExist(userId);
        Event event = eventDao.getEventById(eventId);
        eventDao.checkUserIsInitiator(userId, event);
        eventDao.checkIfEventCanBeModifiedByUser(event);
        eventMapper.updateEventFromUpdateEventUserDto(dto, event);
        eventDao.eventStateUserActionProcessing(event, dto.getStateAction());
        eventDao.saveEvent(event);
        return eventMapper.eventToEventFullDto(event);
    }

    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventDao.getEventById(eventId);
        eventDao.checkEventTimeIsMoreThanHourLaterAfterPublishTime(event);
        eventMapper.updateEventFromAdminDto(dto, event);
        eventDao.eventStateAdminActionProcessing(event, dto.getStateAction());
        eventDao.saveEvent(event);
        return eventMapper.eventToEventFullDto(event);
    }

    private void enrichNewEventWithData(NewEventDto dto, Long userId, Event event) {
        categoryDao.checkCategoryExist(dto.getCategory());
        event.setInitiator(userRepository.getById(userId));
        event.setCategory(categoryRepository.getById(dto.getCategory()));
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setLocation(locationRepository.saveAndFlush(locationMapper.locationDtoToLocation(dto.getLocation())));
    }
}
