package ru.seleznev.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.seleznev.enums.Gender;
import ru.seleznev.enums.HairColor;

@Getter
@AllArgsConstructor
@Schema
public class UserResponse {

    @Schema(description = "User id")
    private Long id;

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
