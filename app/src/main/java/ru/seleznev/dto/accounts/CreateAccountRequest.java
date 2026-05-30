package ru.seleznev.dto.accounts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema
public class CreateAccountRequest {
    @Schema(description = "Owner user id")
    private Long userId;
}
