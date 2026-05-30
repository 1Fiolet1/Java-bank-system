package ru.seleznev.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.seleznev.domain.UserModel;
import ru.seleznev.dto.auth.CreateAdminRequest;
import ru.seleznev.dto.auth.CreateClientRequest;
import ru.seleznev.entities.AuthUser;
import ru.seleznev.entities.User;
import ru.seleznev.enums.Role;
import ru.seleznev.services.UserService;
import ru.seleznev.springdata.SpringDataAuthUserRepository;
import ru.seleznev.springdata.SpringDataUserRepository;

@Service
public class RegistrationService {

    private final SpringDataAuthUserRepository authUserRepository;
    private final SpringDataUserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(SpringDataAuthUserRepository authUserRepository,
                               SpringDataUserRepository userRepository,
                               UserService userService,
                               PasswordEncoder passwordEncoder)
    {
        this.authUserRepository = authUserRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createAdmin(CreateAdminRequest request) {
        if (authUserRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("username already exists");
        }

        AuthUser admin = new AuthUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.ADMIN,
                null
        );

        authUserRepository.save(admin);
    }

    @Transactional
    public UserModel createClient(CreateClientRequest request) {
        if (authUserRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("username already exists");
        }

        UserModel user = new UserModel(
                request.getLogin(),
                request.getName(),
                request.getAge(),
                request.getGender(),
                request.getHairColor()
        );

        UserModel createdUser = userService.createUser(user);
        User userEntity = userRepository.getReferenceById(createdUser.getId());

        AuthUser client = new AuthUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.CLIENT,
                userEntity
        );

        authUserRepository.save(client);

        return createdUser;
    }
}
