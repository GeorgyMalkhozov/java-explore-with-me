package ru.practicum.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.category.dao.CategoryDao;
import ru.practicum.event.dao.EventDao;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventSortType;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.dao.ParticipationRequestDao;
import ru.practicum.user.dao.UserDao;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventDao eventDao;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final CategoryDao categoryDao;
    private final UserDao userDao;
    private final ParticipationRequestDao participationRequestDao;
    private final EntityManager entityManager;

    @Autowired
    public EventService(EventRepository eventRepository, EventMapper eventMapper,
                        EventDao eventDao, LocationRepository locationRepository, LocationMapper locationMapper,
                        CategoryDao categoryDao, UserDao userDao, ParticipationRequestDao participationRequestDao,
                        EntityManager entityManager) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.eventDao = eventDao;
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.categoryDao = categoryDao;
        this.userDao = userDao;
        this.participationRequestDao = participationRequestDao;
        this.entityManager = entityManager;
    }

    public EventFullDto addEvent(NewEventDto dto, Long userId) {
        Event event = eventMapper.newEventDtoToEvent(dto);
        enrichNewEventWithData(dto, userId, event);
        eventDao.saveEvent(event);
        return eventMapper.eventToEventFullDto(event);
    }

    public EventFullDto getPublishedEvent(Long id) {
        eventDao.checkEventExist(id);
        Event event = eventDao.getEventById(id);
        Long views = event.getViews();
        event.setViews(++views);
        eventDao.saveEvent(event);
        return eventMapper.eventToEventFullDto(event);
    }

    public List<EventFullDto> getAllPublishedEventsByFilter(
            String text, List<Integer> categories, Boolean paid, LocalDateTime start, LocalDateTime end,
            Boolean onlyAvailable, String sort, Integer from, Integer size) {

        EventSortType sortConverted = EventSortType.EVENT_DATE;
        if (sort != null) {
            sortConverted = EventSortType.convert(sort.toUpperCase()); }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cr = cb.createQuery(Event.class);
        Root<Event> rootEvent = cr.from(Event.class);
        Predicate predicate = cb.conjunction();
        predicate = getPredicateForPublicEvents(text, categories, paid, start, end, cb, rootEvent, predicate);
        cr.select(rootEvent).where(predicate);
        sortPublicEvents(sortConverted, cb, cr, rootEvent);
        List<Event> events = entityManager.createQuery(cr).setFirstResult(from / size).setMaxResults(size)
                .getResultList();
        participationRequestDao.setConfirmedRequestsCountToEvents(events);
        events = processingOnlyAvailableOption(onlyAvailable, events);
        addViewsForListOfEvents(events);
        eventRepository.saveAllAndFlush(events);
        return events.stream()
                .map(eventMapper::eventToEventFullDto)
                .collect(Collectors.toList());
    }

    public List<EventFullDto> getEventsByAdminWithFilter(
            List<Long> users, List<String> states,
            List<Long> categories, LocalDateTime start,
            LocalDateTime end, Integer from, Integer size) {

        List<EventState> statesList = new ArrayList<>();
        if (states != null) {
            statesList = states.stream().map(state -> EventState.convert(state.toUpperCase()))
                    .collect(Collectors.toList());
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cr = cb.createQuery(Event.class);
        Root<Event> root = cr.from(Event.class);
        Predicate predicate = cb.conjunction();
        predicate = getPredicateForAdminEvents(users, categories, start, end, statesList, cb, root, predicate);
        cr.select(root).where(predicate);
        return entityManager.createQuery(cr).setFirstResult(from / size).setMaxResults(size).getResultList().stream()
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
        eventDao.checkEventTimeIsMoreThanHoursLaterAfterPublishTime(event, 2);
        eventDao.checkUserIsInitiator(userId, event);
        eventDao.checkIfEventCanBeModifiedByState(event);
        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Новая дата события не должна быть раньше чем, через 2 часа");
        }
        eventMapper.updateEventFromUpdateEventUserDto(dto, event);
        eventDao.eventStateUserActionProcessing(event, dto.getStateAction());
        eventDao.saveEvent(event);
        return eventMapper.eventToEventFullDto(event);
    }

    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventDao.getEventById(eventId);
        eventDao.checkIfEventCanBeModifiedByState(event);
        eventDao.checkEventTimeIsMoreThanHoursLaterAfterPublishTime(event, 1);
        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Новая дата события не должна быть раньше чем, через 1 час");
        }
        eventMapper.updateEventFromAdminDto(dto, event);
        eventDao.eventStateAdminActionProcessing(event, dto.getStateAction());
        eventDao.saveEvent(event);
        return eventMapper.eventToEventFullDto(event);
    }

    private static void sortPublicEvents(EventSortType sortConverted, CriteriaBuilder cb, CriteriaQuery<Event> cr,
                                         Root<Event> rootEvent) {
        if (sortConverted == EventSortType.EVENT_DATE) {
            cr.orderBy(cb.desc(rootEvent.get("eventDate")));
        } else if (sortConverted == EventSortType.VIEWS) {
            cr.orderBy(cb.desc(rootEvent.get("views")));
        }
    }

    private static Predicate getPredicateForAdminEvents(List<Long> users, List<Long> categories,
                                                        LocalDateTime start, LocalDateTime end,
                                                        List<EventState> statesList, CriteriaBuilder cb,
                                                        Root<Event> root, Predicate predicate) {
        if (users != null) {
            predicate = cb.and(predicate, root.get("initiator").in(users)); }
        if (!statesList.isEmpty()) {
            predicate = cb.and(predicate, root.get("state").in(statesList)); }
        if (categories != null) {
            predicate = cb.and(predicate, root.get("category").in(categories)); }
        if (start != null) {
            predicate = cb.and(predicate, cb.greaterThan(root.get("eventDate"), start)); }
        if (end != null) {
            predicate = cb.and(predicate, cb.lessThan(root.get("eventDate"), end)); }
        return predicate;
    }

    private static Predicate getPredicateForPublicEvents(String text, List<Integer> categories, Boolean paid,
                                                         LocalDateTime start, LocalDateTime end, CriteriaBuilder cb,
                                                         Root<Event> rootEvent, Predicate predicate) {
        predicate = cb.and(predicate, cb.equal(rootEvent.get("state"), EventState.PUBLISHED));
        if (text != null) {
            predicate = cb.and(predicate, cb.like(cb.lower(rootEvent.get("title")),
                    "%" + text.toLowerCase() + "%"));
            predicate = cb.or(predicate, cb.like(cb.lower(rootEvent.get("annotation")),
                    "%" + text.toLowerCase() + "%"));
        }
        if (categories != null) {
            predicate = cb.and(predicate, rootEvent.get("category").in(categories)); }
        if (paid != null) {
            predicate = paid ? cb.and(predicate, cb.isTrue(rootEvent.get("paid")))
                    : cb.and(predicate, cb.isFalse(rootEvent.get("paid")));
        }
        if (start != null) {
            predicate = cb.and(predicate, cb.greaterThan(rootEvent.get("eventDate"), start)); }
        if (end != null) {
            predicate = cb.and(predicate, cb.lessThan(rootEvent.get("eventDate"), end)); }
        if (start == null && end == null) {
            predicate = cb.and(predicate, cb.greaterThan(rootEvent.get("eventDate"),
                    LocalDateTime.now()));
        }
        return predicate;
    }

    private static void addViewsForListOfEvents(List<Event> events) {
        for (Event event : events) {
            Long views = event.getViews();
            event.setViews(++views);
        }
    }

    private static List<Event> processingOnlyAvailableOption(Boolean onlyAvailable, List<Event> events) {
        if (onlyAvailable != null && onlyAvailable) {
            events = events.stream().filter(e -> e.getConfirmedRequests() == null
                            || e.getParticipantLimit() == 0 || e.getParticipantLimit() > e.getConfirmedRequests())
                    .collect(Collectors.toList());
        }
        return events;
    }

    private void enrichNewEventWithData(NewEventDto dto, Long userId, Event event) {
        userDao.checkUserExist(userId);
        categoryDao.checkCategoryExist(dto.getCategory());
        eventDao.checkEventTimeIsMoreThanHoursLaterAfterPublishTime(event, 2);
        event.setInitiator(userDao.getUserById(userId));
        event.setCategory(categoryDao.getCategoryById(dto.getCategory()));
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setLocation(locationRepository.saveAndFlush(locationMapper.locationDtoToLocation(dto.getLocation())));
        event.setViews(0L);
    }
}
