package ru.practicum.event.mapper;


import org.mapstruct.*;
import org.springframework.validation.annotation.Validated;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.user.mapper.UserMapper;

@Mapper(uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class}, componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "category", ignore = true)
  //  @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
  //  @Mapping(target = "views", ignore = true)
    Event newEventDtoToEvent(NewEventDto dto);

    @Mapping(target = "category")
    @Mapping(target = "createdOn")
    @Mapping(target = "initiator")
    @Mapping(target = "state")
    @Mapping(target = "location")
    @Mapping(target = "publishedOn")
    EventFullDto eventToEventFullDto(Event event);

    @Mapping(target = "category")
    @Mapping(target = "initiator")
    EventShortDto eventToEventShortDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEventFromUpdateEventUserDto(UpdateEventUserRequest updateEventUserRequest, @MappingTarget @Validated Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEventFromAdminDto(UpdateEventAdminRequest updateAdminDto, @MappingTarget @Validated Event event);
}
