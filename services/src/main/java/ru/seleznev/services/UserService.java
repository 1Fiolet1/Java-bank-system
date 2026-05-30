package ru.seleznev.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.seleznev.domain.UserModel;
import ru.seleznev.enums.Gender;
import ru.seleznev.enums.HairColor;
import ru.seleznev.exceptions.EntityNotFoundException;
import ru.seleznev.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserModel createUser(UserModel user) {
        if (user == null) {
            throw new IllegalArgumentException("user is null");
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserModel getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
    }

    @Transactional
    public void addFriend(Long userId, Long friendId) {
        UserModel user = userRepository.findWithFriendsById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        UserModel friend = userRepository.findWithFriendsById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("friend not found"));

        user.addFriend(friend);
        friend.addFriend(user);

        userRepository.save(user);
        userRepository.save(friend);
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        UserModel user = userRepository.findWithFriendsById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        UserModel friend = userRepository.findWithFriendsById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("friend not found"));

        user.removeFriend(friend);
        friend.removeFriend(user);

        userRepository.save(user);
        userRepository.save(friend);
    }

    @Transactional(readOnly = true)
    public List<UserModel> getUsers(HairColor hairColor, Gender gender) {
        if (hairColor != null && gender != null) {
            return userRepository.findByHairColorAndGender(hairColor, gender);
        }

        if (hairColor != null) {
            return userRepository.findByHairColor(hairColor);
        }

        if (gender != null) {
            return userRepository.findByGender(gender);
        }

        return  userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UserModel> getFriendsByUserId(Long userId) {
        UserModel user = userRepository.findWithFriendsById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        return user.getFriends().stream().toList();
    }
}
