package ru.seleznev.domain;

import lombok.*;
import ru.seleznev.enums.Gender;
import ru.seleznev.enums.HairColor;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserModel {

    @EqualsAndHashCode.Include
    private Long id;

    private String login;

    private String name;

    private Integer age;

    private Gender gender;

    private HairColor hairColor;

    private Set<UserModel> friends = new HashSet<>();

    public UserModel(String login, String name, Integer age, Gender gender, HairColor hairColor) {
        this.login = login;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.hairColor = hairColor;
    }

    public void addFriend(UserModel friend) {
        friends.add(friend);
    }

    public void removeFriend(UserModel friend) {
        friends.remove(friend);
    }
}
