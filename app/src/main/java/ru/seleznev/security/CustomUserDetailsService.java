package ru.seleznev.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.seleznev.entities.AuthUser;
import ru.seleznev.springdata.SpringDataAuthUserRepository;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SpringDataAuthUserRepository authUserRepository;

    @Autowired
    public CustomUserDetailsService(SpringDataAuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser authUser = authUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        User user = new User(
                authUser.getUsername(),
                authUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + authUser.getRole().name()))
        );

        return user;
    }
}
