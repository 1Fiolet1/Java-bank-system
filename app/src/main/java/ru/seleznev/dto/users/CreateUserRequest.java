package ru.seleznev.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import ru.seleznev.enums.Gender;
import ru.seleznev.enums.HairColor;

@Getter
@Setter
@Schema
public class CreateUserRequest {

    @Schema(description = "User login")
    private String login;

    @Schema(description = "User name")
    private String name;

    @Schema(description = "User age")
    private Integer age;

    @Schema(description = "User gender")
    private Gender gender;

    @Schema(description = "User hair color")
    private HairColor hairColor;
}
