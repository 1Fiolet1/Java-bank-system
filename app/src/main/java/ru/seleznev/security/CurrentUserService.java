package ru.seleznev.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.seleznev.entities.AuthUser;
import ru.seleznev.exceptions.EntityNotFoundException;
import ru.seleznev.springdata.SpringDataAuthUserRepository;

@Service
public class CurrentUserService {

    private final SpringDataAuthUserRepository authUserRepository;

    @Autowired
    public CurrentUserService(SpringDataAuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    public AuthUser getCurrentAuthUser(Authentication authentication) {
        return authUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("auth user not found"));
    }

    public Long getCurrentClientId(Authentication authentication) {
        AuthUser authUser = getCurrentAuthUser(authentication);

        if (authUser.getUser() == null) {
            throw new IllegalArgumentException("admin has no client profile");
        }

        return authUser.getUser().getId();
    }
}
