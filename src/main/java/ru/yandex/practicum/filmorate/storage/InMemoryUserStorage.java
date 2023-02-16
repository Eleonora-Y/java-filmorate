package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.id.UserId;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utilit.WarnAndThrowException;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final UserId id = new UserId();

    public void validate(User user) {
        if (user.getEmail().isEmpty() ||
                user.getEmail() == null ||
                !user.getEmail().contains("@")) {

            String message = "Адрес эл. почты пустой или не хватает символа @";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (user.getLogin().isBlank()) {
            String message = "Логин пустой или содержит пробелы";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String message = "Дата рождения указана неверно";
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    @Override
    public User create(User user) {
        if (users.containsKey(user.getId())) {
            String message = String.format("Пользователь существует: %s", user.getName());
            log.warn(message);
            throw new ValidationException(message);
        }
        validate(user);
        user.setId(id.getUserId());
        if (user.getName() == null || user.getName().isEmpty()) {
            String a = user.getLogin();
            user.setName(a);
            users.put(user.getId(), user);
            log.info("Добавлен : {}", user);
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            String message = String.format("Пользователь не существует: %s", user.getName());
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        validate(user);
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    @Override
    public List<User> findAll() {
        if (users.isEmpty()) {
            String message = "Пользователи не существуют";
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long id) {
        if (!users.containsKey(id)) {
            WarnAndThrowException.logAndThrowExceptionIfIdFilmNotExist(id);
        }

        return users.get(id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (!users.containsKey(userId)) {
            WarnAndThrowException.logAndThrowExceptionIfIdFilmNotExist(userId);
        }
        if (!users.containsKey(friendId)) {
            WarnAndThrowException.logAndThrowExceptionIfIdFilmNotExist(friendId);
        }
        User userFriend = users.get(friendId);
        User user=users.get(userId);
        user.getFriendsId().add(friendId);
        userFriend.getFriendsId().add(userId);

        log.info("Пользователь {} добавлен в друзья пользователю {}",
                userFriend.getName(),
                user.getName());
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        if (!users.containsKey(userId)) {
            WarnAndThrowException.logAndThrowExceptionIfIdFilmNotExist(userId);
        }
        if (!users.containsKey(friendId)) {
            WarnAndThrowException.logAndThrowExceptionIfIdFilmNotExist(friendId);
        }
        User userFriend = users.get(friendId);
        User user=users.get(userId);
        user.getFriendsId().remove(friendId);
        userFriend.getFriendsId().remove(userId);

        log.info("Пользователь {} удален из друзей пользователя {}",
                userFriend.getName(),
                user.getName());

    }

    @Override
    public List<User> getFriends(long id) {
        if (!users.containsKey(id)) {
            WarnAndThrowException.logAndThrowExceptionIfIdFilmNotExist(id);
        }
        List<Long> idFriends = new ArrayList<>(users.get(id).getFriendsId());
        List<User> friends = new ArrayList<>();
        for (Long idFriend : idFriends) {
            friends.add(users.get(idFriend));
        }
        log.info("Текущее количество друзей пользователя {}: {}",
                users.get(id).getName(),
                users.get(id).getFriendsId().size());
        return friends;
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        if (!users.containsKey(id)) {
            WarnAndThrowException.logAndThrowExceptionIfIdFilmNotExist(id);
        }
        if (!users.containsKey(otherId)) {
            WarnAndThrowException.logAndThrowExceptionIfIdFilmNotExist(otherId);
        }
        List<User> commonFriends = new ArrayList<>();
        for (Long userId : users.get(id).getFriendsId()) {
            if (users.get(otherId).getFriendsId().contains(userId)) {
                commonFriends.add(users.get(userId));
            }
        }
        log.info("Текущее количество общих друзей у пользователей {} и {}: {}",
                users.get(id).getName(),
                users.get(otherId).getName(),
                commonFriends.size());
        return commonFriends;
    }

}

