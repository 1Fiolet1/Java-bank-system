package ru.seleznev.mappers;

import org.springframework.stereotype.Component;

import ru.seleznev.domain.UserModel;
import ru.seleznev.entities.User;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class UserEntityMapper {

    public UserModel toModel(User user) {
        return new UserModel(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getAge(),
                user.getGender(),
                user.getHairColor(),
                user.getFriends().stream()
                        .map(this::toModelWithoutFriends)
                        .collect(Collectors.toSet())
        );
    }

    public UserModel toModelWithoutFriends(User user) {
        return new UserModel(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getAge(),
                user.getGender(),
                user.getHairColor(),
                new HashSet<>()
        );
    }

    public User toEntity(UserModel model) {
        User user = new User(
                model.getLogin(),
                model.getName(),
                model.getAge(),
                model.getGender(),
                model.getHairColor()
        );

        user.setId(model.getId());

        model.getFriends().stream()
                .map(this::toEntityWithoutFriends)
                .forEach(user::addFriend);

        return user;
    }

    public User toEntityWithoutFriends(UserModel model) {
        User user = new User(
                model.getLogin(),
                model.getName(),
                model.getAge(),
                model.getGender(),
                model.getHairColor()
        );

        user.setId(model.getId());

        return user;
    }

}
