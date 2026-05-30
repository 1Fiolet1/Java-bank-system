package ru.seleznev.dto.accounts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.seleznev.dto.operations.OperationResponse;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema
public class AccountWithOperationsResponse {

    @Schema(description = "Account id")
    private Long id;

    @Schema(description = "Account balance")
    private BigDecimal balance;

    @Schema(description = "Owner user id")
    private Long ownerId;

    @Schema(description = "Account operations")
    private List<OperationResponse> operations;
}
