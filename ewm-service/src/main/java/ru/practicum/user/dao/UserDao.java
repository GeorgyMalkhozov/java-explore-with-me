package ru.practicum.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.exceptions.NoObjectsFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

@Component
public class UserDao {

    private final UserRepository userRepository;

    public UserDao(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NoObjectsFoundException("Пользователь с id = " + id + " не существует"));
    }
}
