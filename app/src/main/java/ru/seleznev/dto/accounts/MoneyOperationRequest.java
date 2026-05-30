package ru.seleznev.dto.accounts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema
public class MoneyOperationRequest {
    @Schema(description = "Money amount")
    private BigDecimal amount;
}
