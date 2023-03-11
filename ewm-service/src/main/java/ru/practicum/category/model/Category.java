package ru.practicum.category.model;

import org.springframework.validation.annotation.Validated;

import javax.persistence.*;

@Validated
@Entity
@Table(name = "categories", schema = "public", uniqueConstraints = { @UniqueConstraint(name = "UniqueName",
        columnNames = { "name" }) })
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
