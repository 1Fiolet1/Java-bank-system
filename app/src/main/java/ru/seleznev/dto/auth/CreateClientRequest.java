package ru.seleznev.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import ru.seleznev.enums.Gender;
import ru.seleznev.enums.HairColor;

@Getter
@Setter
@Schema
public class CreateClientRequest {

    @Schema(description = "Client username")
    private String username;

    @Schema(description = "Client password")
    private String password;

    @Schema(description = "Client login")
    private String login;

    @Schema(description = "Client name")
    private String name;

    @Schema(description = "Client age")
    private Integer age;

    @Schema(description = "Client gender")
    private Gender gender;

    @Schema(description = "Client hair color")
    private HairColor hairColor;
}
