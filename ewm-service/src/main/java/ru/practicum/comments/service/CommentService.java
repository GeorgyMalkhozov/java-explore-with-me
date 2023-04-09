package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    CommentDto addComment(NewCommentDto newCommentDto, Long userId, Long eventId); // private

    CommentDto getComment(Long commentId); // private

    void deleteCommentByUser(Long userId, Long commentId); // private

    CommentDto blockCommentByAdmin(Long commentId); // admin

    List<CommentDto> getAllCommentsByUser(Long userId); // public

    List<CommentDto> getAllCommentsForEvent(Long eventId); // public

    List<CommentDto> getAllCommentsByAdmin(String text, List<Long> commentators, List<Long> events, LocalDateTime start,
                                           LocalDateTime end, Boolean showOnlyCanceled, Integer from,
                                           Integer size); // admin
}
