package ru.practicum.comments.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.exceptions.NoObjectsFoundException;
import ru.practicum.exceptions.ValidationException;

import java.util.Objects;

@Component
public class CommentDao {

    private final CommentRepository commentRepository;

    public CommentDao(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void saveComment(Comment comment) {
        try {
            commentRepository.saveAndFlush(comment);
        } catch (TransactionSystemException e) {
            throw new ValidationException("поле не может быть пустым");
        }
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new NoObjectsFoundException("Комментарий с id = " + id + " не существует"));
    }

    public void checkUserIsCommentator(Long userId, Comment comment) {
        if (!Objects.equals(comment.getCommentator().getId(), userId)) {
            throw new NoObjectsFoundException("Пользователь не является автором комментария");
        }
    }
}
