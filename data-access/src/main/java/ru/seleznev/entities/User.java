package ru.seleznev.entities;

import jakarta.persistence.*;
import lombok.*;
import ru.seleznev.enums.Gender;
import ru.seleznev.enums.HairColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"friends", "accounts"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color", nullable = false)
    private HairColor hairColor;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")

    )
    private Set<User> friends = new HashSet<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    public User(String login, String name, int age, Gender gender, HairColor hairColor) {
        this.login = login;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.hairColor = hairColor;
    }

    public void addFriend(User friend) {
        this.friends.add(friend);
    }

    public void removeFriend(User friend) {
        this.friends.remove(friend);
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
        account.setOwner(this);
    }

    public void removeAccount(Account account) {
        this.accounts.remove(account);
        account.setOwner(null);
    }
}