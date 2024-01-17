package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")

public class UserController {

    private final UserService userService;

//    private void validate(User data) {
//        if (data.getEmail().isBlank() || !data.getEmail().contains("@")) {
//            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
//        }
//        if (data.getLogin().isBlank()) {
//            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
//        }
//        if (LocalDate.now().isBefore(data.getBirthday())) {
//            throw new ValidationException("Дата рождения не может быть в будущем");
//        }
//    }

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
        //validate(updatedUser);
        userService.update(updatedUser);
        return updatedUser;
    }

//    @PutMapping
//    @ResponseStatus
//    public User update(@Valid @RequestBody User updatedUser) {
//        validate(updatedUser);
//        Optional<User> user = Optional.ofNullable(userService.getUserById(updatedUser.getId()));
//        if(user.isPresent()) {
//            userService.update(updatedUser);
//        } else {
//            throw new NotFoundException("Такого пользователя не существует");
//        }
//        return updatedUser;
//    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        //validate(user);
        return userService.create(user);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }


    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(value = "id") long id,
                                       @PathVariable(value = "otherId") long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable(value = "id") long id,
                             @PathVariable(value = "friendId") long friendId) {
        userService.addToFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @DeleteMapping("/{id}")
    public User removeFromUsers(@PathVariable Long id) {
        return userService.removeUser(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFromFriends(@PathVariable(value = "id") long id,
                                  @PathVariable(value = "friendId") long friendId) {
        userService.removeFromFriends(id, friendId);
    }
}
