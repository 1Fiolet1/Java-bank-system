package ru.seleznev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.seleznev.dto.auth.CreateAdminRequest;
import ru.seleznev.security.RegistrationService;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final RegistrationService registrationService;

    @Autowired
    public AdminController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create admin")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    public void createAdmin(@RequestBody CreateAdminRequest request) {
        registrationService.createAdmin(request);
    }
}
