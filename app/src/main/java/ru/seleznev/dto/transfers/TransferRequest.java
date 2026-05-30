package ru.seleznev.dto.transfers;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema
public class TransferRequest {

    @Schema(description = "Sender account id")
    private Long fromAccountId;

    @Schema(description = "Recipient account id")
    private Long toAccountId;

    @Schema(description = "Transfer amount")
    private BigDecimal amount;
}
