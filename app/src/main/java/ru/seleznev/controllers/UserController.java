package ru.seleznev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.seleznev.domain.UserModel;
import ru.seleznev.dto.auth.CreateClientRequest;
import ru.seleznev.dto.users.UserDtoMapper;
import ru.seleznev.dto.users.UserResponse;
import ru.seleznev.enums.Gender;
import ru.seleznev.enums.HairColor;
import ru.seleznev.security.RegistrationService;
import ru.seleznev.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final RegistrationService registrationService;

    @Autowired
    public UserController(UserService userService, UserDtoMapper userDtoMapper, RegistrationService registrationService) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
        this.registrationService = registrationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create client")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    public UserResponse createUser(@RequestBody CreateClientRequest request) {
        UserModel createdUser = registrationService.createClient(request);

        return userDtoMapper.toResponse(createdUser);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public UserResponse getUserById(@PathVariable Long userId) {
        return userDtoMapper.toResponse(userService.getUserById(userId));
    }

    @GetMapping
    @Operation(summary = "Get users")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    public List<UserResponse> getUsers(@RequestParam(required = false) HairColor hairColor,
                                       @RequestParam(required = false) Gender gender)
    {
        return userService.getUsers(hairColor,gender).stream()
                .map(userDtoMapper::toResponse)
                .toList();
    }
}
