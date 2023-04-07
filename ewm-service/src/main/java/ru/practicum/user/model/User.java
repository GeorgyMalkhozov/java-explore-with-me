package ru.practicum.user.model;

import org.springframework.validation.annotation.Validated;

import javax.persistence.*;

@Validated
@Entity
@Table(name = "users", schema = "public", uniqueConstraints = { @UniqueConstraint(name = "UniqueEmail",
        columnNames = { "email" }) })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
