package ru.seleznev.adapters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.seleznev.domain.UserModel;
import ru.seleznev.enums.Gender;
import ru.seleznev.enums.HairColor;
import ru.seleznev.mappers.UserEntityMapper;
import ru.seleznev.repositories.UserRepository;
import ru.seleznev.springdata.SpringDataUserRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository userRepository;
    private final UserEntityMapper mapper;

    @Autowired
    public UserRepositoryAdapter(SpringDataUserRepository userRepository, UserEntityMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public List<UserModel> findByGender(Gender gender) {
        return userRepository.findByGender(gender).stream()
                .map(mapper::toModelWithoutFriends)
                .toList();
    }

    @Override
    public List<UserModel> findByHairColor(HairColor hairColor) {
        return userRepository.findByHairColor(hairColor).stream()
                .map(mapper::toModelWithoutFriends)
                .toList();
    }

    @Override
    public List<UserModel> findByHairColorAndGender(HairColor hairColor, Gender gender) {
        return userRepository.findByHairColorAndGender(hairColor, gender).stream()
                .map(mapper::toModelWithoutFriends)
                .toList();
    }

    @Override
    public UserModel save(UserModel user) {
        return mapper.toModelWithoutFriends(userRepository.save(mapper.toEntity(user)));
    }

    @Override
    public Optional<UserModel> findById(Long id) {
        return userRepository.findById(id).map(mapper::toModelWithoutFriends);
    }

    @Override
    public Optional<UserModel> findByLogin(String login) {
        return userRepository.findByLogin(login).map(mapper::toModelWithoutFriends);
    }

    @Override
    public List<UserModel> findAll() {
        return userRepository.findAll().stream()
                .map(mapper::toModelWithoutFriends)
                .toList();
    }

    @Override
    public Optional<UserModel> findWithFriendsById(Long id) {
        return userRepository.findWithFriendsById(id).map(mapper::toModel);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
