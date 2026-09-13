package ru.seleznev.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.seleznev.domain.UserModel;
import ru.seleznev.enums.Gender;
import ru.seleznev.enums.HairColor;
import ru.seleznev.exceptions.EntityNotFoundException;
import ru.seleznev.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserSavesUser() {
        UserModel user = user(1L);
        when(userRepository.save(user)).thenReturn(user);

        assertSame(user, userService.createUser(user));
    }

    @Test
    void createUserRejectsNullUser() {
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(null));

        verify(userRepository, never()).save(null);
    }

    @Test
    void getUserByIdReturnsExistingUser() {
        UserModel user = user(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertSame(user, userService.getUserById(1L));
    }

    @Test
    void getUserByIdThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void addFriendCreatesBidirectionalFriendshipAndSavesBothUsers() {
        UserModel user = user(1L);
        UserModel friend = user(2L);

        when(userRepository.findWithFriendsById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findWithFriendsById(2L)).thenReturn(Optional.of(friend));

        userService.addFriend(1L, 2L);

        assertTrue(user.getFriends().contains(friend));
        assertTrue(friend.getFriends().contains(user));
        verify(userRepository).save(user);
        verify(userRepository).save(friend);
    }

    @Test
    void removeFriendRemovesBidirectionalFriendshipAndSavesBothUsers() {
        UserModel user = user(1L);
        UserModel friend = user(2L);
        user.addFriend(friend);
        friend.addFriend(user);

        when(userRepository.findWithFriendsById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findWithFriendsById(2L)).thenReturn(Optional.of(friend));

        userService.removeFriend(1L, 2L);

        assertTrue(user.getFriends().isEmpty());
        assertTrue(friend.getFriends().isEmpty());
        verify(userRepository).save(user);
        verify(userRepository).save(friend);
    }

    @Test
    void getUsersByHairColorAndGenderUsesCombinedFilter() {
        List<UserModel> users = List.of(user(1L));
        when(userRepository.findByHairColorAndGender(HairColor.BLACK, Gender.MALE)).thenReturn(users);

        assertSame(users, userService.getUsers(HairColor.BLACK, Gender.MALE));
    }

    @Test
    void getUsersByHairColorUsesHairColorFilter() {
        List<UserModel> users = List.of(user(1L));
        when(userRepository.findByHairColor(HairColor.RED)).thenReturn(users);

        assertSame(users, userService.getUsers(HairColor.RED, null));
    }

    @Test
    void getUsersByGenderUsesGenderFilter() {
        List<UserModel> users = List.of(user(1L));
        when(userRepository.findByGender(Gender.FEMALE)).thenReturn(users);

        assertSame(users, userService.getUsers(null, Gender.FEMALE));
    }

    @Test
    void getUsersWithoutFiltersReturnsAllUsers() {
        List<UserModel> users = List.of(user(1L));
        when(userRepository.findAll()).thenReturn(users);

        assertSame(users, userService.getUsers(null, null));
    }

    @Test
    void getFriendsByUserIdReturnsFriends() {
        UserModel user = user(1L);
        UserModel friend = user(2L);
        user.addFriend(friend);

        when(userRepository.findWithFriendsById(1L)).thenReturn(Optional.of(user));

        List<UserModel> friends = userService.getFriendsByUserId(1L);

        assertEqualsOneFriend(friend, friends);
    }

    private static UserModel user(Long id) {
        UserModel user = new UserModel();
        user.setId(id);
        return user;
    }

    private static void assertEqualsOneFriend(UserModel expectedFriend, List<UserModel> friends) {
        assertTrue(friends.size() == 1);
        assertSame(expectedFriend, friends.get(0));
    }
}
