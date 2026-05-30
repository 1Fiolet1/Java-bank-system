package ru.seleznev.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                .requestMatchers("/me" ,"/me/**").hasRole("CLIENT")

                .requestMatchers(HttpMethod.POST, "/admins").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/users/*").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/accounts").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/accounts").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/accounts/by-user/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/accounts/*").hasRole("ADMIN")


                .anyRequest().denyAll()
        );

        http.formLogin(form -> form
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) ->
                        response.setStatus(HttpServletResponse.SC_OK))
                .failureHandler((request, response, exception) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) ->
                        response.setStatus(HttpServletResponse.SC_OK))
        );

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        response.sendError(HttpServletResponse.SC_FORBIDDEN))
        );

        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
