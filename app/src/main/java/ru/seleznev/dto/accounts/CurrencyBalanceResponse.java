package ru.seleznev.dto.accounts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
@Schema
public class CurrencyBalanceResponse {

    @Schema(description = "Account id")
    private Long accountId;

    @Schema(description = "Currency code")
    private String currency;

    @Schema(description = "Balance in specified currency")
    private BigDecimal balance;

    @Schema(description = "Exchange rate used")
    private BigDecimal rateToRub;

    @Schema(description = "When the rate was published")
    private Instant rateTimestamp;
}
