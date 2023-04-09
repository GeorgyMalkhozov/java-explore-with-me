package ru.practicum.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.comments.enums.CommentState;

import java.time.LocalDateTime;

public class CommentDto {

    private Long id;
    private Long event;
    private Long commentator;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private CommentState state;
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEvent() {
        return event;
    }

    public void setEvent(Long event) {
        this.event = event;
    }

    public Long getCommentator() {
        return commentator;
    }

    public void setCommentator(Long commentator) {
        this.commentator = commentator;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public CommentState getState() {
        return state;
    }

    public void setState(CommentState state) {
        this.state = state;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
