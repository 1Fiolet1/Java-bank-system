package ru.seleznev.dto.errors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema
@AllArgsConstructor
public class ErrorResponse {
    @Schema(description = "Error message")
    private String message;
}
