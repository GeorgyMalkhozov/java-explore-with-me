package ru.practicum.comments.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dao.CommentDao;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.enums.CommentState;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.event.dao.EventDao;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NoObjectsFoundException;
import ru.practicum.user.dao.UserDao;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentDao commentDao;
    private final UserDao userDao;
    private final EventDao eventDao;
    private final CommentMapper commentMapper;
    private final EntityManager entityManager;

    public CommentServiceImpl(CommentRepository commentRepository, CommentDao commentDao, UserDao userDao,
                              EventDao eventDao, CommentMapper commentMapper, EntityManager entityManager) {
        this.commentRepository = commentRepository;
        this.commentDao = commentDao;
        this.userDao = userDao;
        this.eventDao = eventDao;
        this.commentMapper = commentMapper;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public CommentDto addComment(NewCommentDto newCommentDto, Long userId, Long eventId) {
        Comment comment = commentMapper.newCommentToComment(newCommentDto);
        enrichNewCommentWithData(eventId, userId, comment);
        commentDao.saveComment(comment);
        return commentMapper.commentToDto(comment);
    }

    @Override
    public CommentDto getComment(Long commentId) {
        Comment comment = commentDao.getCommentById(commentId);
        if (comment.getState().equals(CommentState.BLOCKED)) {
            throw new NoObjectsFoundException("Комментарий заблокирован администратором");
        }
        return commentMapper.commentToDto(comment);
    }

    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = commentDao.getCommentById(commentId);
        commentDao.checkUserIsCommentator(userId, comment);
        if (comment.getState().equals(CommentState.BLOCKED)) {
            throw new ConflictException("Нельзя удалить заблокированный администратором комментарий");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto blockCommentByAdmin(Long commentId) {
        Comment comment = commentDao.getCommentById(commentId);
        if (comment.getState().equals(CommentState.PUBLISHED)) {
            comment.setState(CommentState.BLOCKED);
            commentDao.saveComment(comment);
        } else {
            throw new ConflictException("Комментарий уже заблокирован");
        }
        return commentMapper.commentToDto(comment);
    }

    @Override
    public List<CommentDto> getAllCommentsByUser(Long userId) {
        userDao.getUserById(userId);
        List<Comment> comments = commentRepository.findAllByCommentatorIdAndState(userId, CommentState.PUBLISHED);
        return comments.stream()
                .map(commentMapper::commentToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsForEvent(Long eventId) {
        Event event = eventDao.getEventById(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие неопубликовано");
        }
        List<Comment> comments = commentRepository.findAllByEventIdAndState(eventId, CommentState.PUBLISHED);
        return comments.stream()
                .map(commentMapper::commentToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsByAdmin(String text, List<Long> commentators, List<Long> events,
                                                  LocalDateTime start, LocalDateTime end, Boolean showOnlyCanceled,
                                                  Integer from, Integer size) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> cr = cb.createQuery(Comment.class);
        Root<Comment> root = cr.from(Comment.class);
        Predicate predicate = cb.conjunction();
        predicate = getPredicateForAdminComments(text, commentators, events, start, end, showOnlyCanceled, cb, root,
                predicate);
        cr.select(root).where(predicate).orderBy(cb.desc(root.get("created")));
        return entityManager.createQuery(cr).setFirstResult(from / size).setMaxResults(size).getResultList().stream()
                .map(commentMapper::commentToDto)
                .collect(Collectors.toList());
    }

    private static Predicate getPredicateForAdminComments(String text, List<Long> commentators, List<Long> events,
                                                        LocalDateTime start, LocalDateTime end,
                                                        Boolean onlyCanceled, CriteriaBuilder cb,
                                                        Root<Comment> root, Predicate predicate) {
        if (text != null) {
            predicate = cb.and(predicate, cb.like(cb.lower(root.get("text")),
                    "%" + text.toLowerCase() + "%"));
        }
        if (commentators != null) {
            predicate = cb.and(predicate, root.get("commentator").in(commentators));
        }
        if (onlyCanceled != null && onlyCanceled) {
            predicate = cb.and(predicate, cb.equal(root.get("state"), CommentState.BLOCKED));
        } else {
            predicate = cb.and(predicate, cb.equal(root.get("state"), CommentState.PUBLISHED));
        }
        if (events != null) {
            predicate = cb.and(predicate, root.get("event").in(events));
        }
        if (start != null) {
            predicate = cb.and(predicate, cb.greaterThan(root.get("created"), start));
        }
        if (end != null) {
            predicate = cb.and(predicate, cb.lessThan(root.get("created"), end));
        }
        return predicate;
    }

    private void enrichNewCommentWithData(Long eventId, Long userId, Comment comment) {
        Event event = eventDao.getEventById(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя комментировать неопубликованное событие");
        }
        comment.setEvent(event);
        comment.setCommentator(userDao.getUserById(userId));
        comment.setCreated(LocalDateTime.now());
        comment.setState(CommentState.PUBLISHED);
    }
}
