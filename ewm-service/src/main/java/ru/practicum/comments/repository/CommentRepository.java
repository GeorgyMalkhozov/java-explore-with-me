package ru.practicum.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.enums.CommentState;
import ru.practicum.comments.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventIdAndState(Long eventId, CommentState state);

    List<Comment> findAllByCommentatorIdAndState(Long userId, CommentState state);
}
