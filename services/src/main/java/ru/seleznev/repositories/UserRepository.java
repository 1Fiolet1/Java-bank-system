package ru.seleznev.repositories;

import ru.seleznev.domain.UserModel;
import ru.seleznev.enums.Gender;
import ru.seleznev.enums.HairColor;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<UserModel> findByGender(Gender gender);

    List<UserModel> findByHairColor(HairColor hairColor);

    List<UserModel> findByHairColorAndGender(HairColor hairColor, Gender gender);

    UserModel save(UserModel user);

    Optional<UserModel> findById(Long id);

    Optional<UserModel> findByLogin(String login);

    List<UserModel> findAll();

    Optional<UserModel> findWithFriendsById(Long id);

    void deleteById(Long id);
}
