package ru.seleznev.springdata;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.seleznev.entities.User;
import ru.seleznev.enums.Gender;
import ru.seleznev.enums.HairColor;

import java.util.List;
import java.util.Optional;


public interface SpringDataUserRepository extends JpaRepository<User, Long> {

    @Override
    User save(User user);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "friends")
    Optional<User> findWithFriendsById(Long id);

    List<User> findByHairColor(HairColor hairColor);

    Optional<User> findByLogin(String login);

    List<User> findByGender(Gender gender);

    List<User> findByHairColorAndGender(HairColor color, Gender gender);
}
