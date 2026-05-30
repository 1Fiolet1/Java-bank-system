package ru.seleznev.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.seleznev.entities.AuthUser;
import ru.seleznev.enums.Role;
import ru.seleznev.springdata.SpringDataAuthUserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final SpringDataAuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminInitializer(SpringDataAuthUserRepository authUserRepository, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String userName = "admin";

        if (authUserRepository.existsByUsername(userName)) {
            return;
        }

        AuthUser admin = new AuthUser(
                userName,
                passwordEncoder.encode("admin"),
                Role.ADMIN,
                null
        );

        authUserRepository.save(admin);
    }
}
