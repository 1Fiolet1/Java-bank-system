package ru.seleznev.dto.operations;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.seleznev.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema
public class OperationResponse {

    @Schema(description = "Operation id")
    private Long id;

    @Schema(description = "Operation type")
    private OperationType type;

    @Schema(description = "Operation amount")
    private BigDecimal amount;

    @Schema(description = "Operation commission")
    private BigDecimal commission;

    @Schema(description = "Operation creation time")
    private LocalDateTime createdAt;

    @Schema(description = "Account id")
    private Long accountId;
}
