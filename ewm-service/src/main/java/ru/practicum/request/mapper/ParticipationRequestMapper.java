package ru.practicum.request.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.user.mapper.UserMapper;

@Mapper(uses = {UserMapper.class, EventMapper.class}, componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto participationRequestToDto(ParticipationRequest participationRequest);
}
