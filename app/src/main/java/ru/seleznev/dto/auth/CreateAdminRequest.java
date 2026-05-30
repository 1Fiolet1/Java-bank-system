package ru.seleznev.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema
public class CreateAdminRequest {

    @Schema(description = "Admin username")
    private String username;

    @Schema(description = "Admin password")
    private String password;
}
