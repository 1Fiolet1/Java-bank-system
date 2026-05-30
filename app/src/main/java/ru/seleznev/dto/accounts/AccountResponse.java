package ru.seleznev.dto.accounts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Schema
public class AccountResponse {
    @Schema(description = "Account id")
    private Long id;

    @Schema(description = "Account balance")
    private BigDecimal balance;

    @Schema(description = "Owner user id")
    private Long ownerId;
}
