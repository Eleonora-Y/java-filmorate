package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.id.UserId;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
@Data
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final UserId userId = new UserId();

    private static final LocalDate loc = LocalDate.now();


    @PostMapping()
    public User create(@RequestBody User user) {

        if (users.containsKey(user.getId())) {
            logWarnAndThrowException("Пользователь уже существует");
        }

        if (user.getEmail().isEmpty() ||
                user.getEmail() == null ||
                !user.getEmail().contains("@")) {
            logWarnAndThrowException("Адрес эл. почты пустой или не хватает символа @");
        }

        if (user.getLogin().isBlank()) {
            logWarnAndThrowException("Логин пустой или содержит пробелы");
        }

        if (user.getBirthday().isAfter(loc)) {
            logWarnAndThrowException("Дата рождения указана неверно");
        }

        user.setId(userId.getUserId());

        if (user.getName() == null) {
            // if (user.getName().isEmpty() || user.getName() == null) {
            String a = user.getLogin();
            user.setName(a);
            users.put(user.getId(), user);
            log.info("Добавлен : {}", user);
        }
        if (user.getName().isEmpty()) {

            String a = user.getLogin();
            user.setName(a);
            users.put(user.getId(), user);
            log.info("Добавлен : {}", user);
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }


    @PutMapping()
    public User update(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            logWarnAndThrowException("Пользователь не существует");
        }
        if (users.containsKey(user.getId())) {
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            log.info("Добавлен пользователь: {}", user);
            users.put(user.getId(), user);
        }
        return user;
    }

    @GetMapping()
    public List<User> getAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    private void logWarnAndThrowException(String message) {
        log.warn(message);
        throw new ValidationException(message);
    }


}

