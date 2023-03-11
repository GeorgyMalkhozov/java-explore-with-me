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

    public void checkUserExist(Long userId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new NoObjectsFoundException("Пользователь с id = " + userId + " не существует");
        }
    }

    public User getUserById(Long id) {
        checkUserExist(id);
        return userRepository.getById(id);
    }
}
