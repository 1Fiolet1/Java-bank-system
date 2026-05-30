package ru.seleznev.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seleznev.enums.Role;

@Entity
@NoArgsConstructor
@Table(name = "auth_users")
@Getter
@Setter
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public AuthUser(String username, String password, Role role, User user) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.user = user;
    }


}
