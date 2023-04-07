package ru.practicum.compilation.model;

import org.springframework.validation.annotation.Validated;
import ru.practicum.event.model.Event;

import javax.persistence.*;
import java.util.Set;

@Validated
@Entity
@Table(name = "compilations", schema = "public", uniqueConstraints = { @UniqueConstraint(name = "UniqueTitle",
        columnNames = { "title" }) })
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "pinned", nullable = false)
    private boolean pinned;
    @ManyToMany
    @JoinTable(
            name = "compilations_of_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }
}
