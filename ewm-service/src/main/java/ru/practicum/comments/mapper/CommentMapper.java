package ru.practicum.comments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.user.mapper.UserMapper;

@Mapper(uses = {UserMapper.class, EventMapper.class}, componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "commentator", source = "commentator.id")
    CommentDto commentToDto(Comment comment);

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "commentator", ignore = true)
    @Mapping(target = "state", ignore = true)
    Comment newCommentToComment(NewCommentDto newCommentDto);
}
