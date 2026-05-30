package ru.seleznev.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seleznev.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OperationModel {

    private Long id;

    private OperationType type;

    private BigDecimal commission = BigDecimal.ZERO;

    private BigDecimal amount;

    private LocalDateTime createdAt;

    private Long accountId;

    public OperationModel(Long id, OperationType type, BigDecimal amount, BigDecimal commission, LocalDateTime createdAt, Long accountId) {
        this.id = id;
        this.type = type;
        this.commission = commission;
        this.amount = amount;
        this.createdAt = createdAt;
        this.accountId = accountId;
    }

    public OperationModel(OperationType type, BigDecimal amount, BigDecimal commission, Long accountId) {
        this.type = type;
        this.amount = amount;
        this.commission = commission;
        this.accountId = accountId;
        this.createdAt = LocalDateTime.now();
    }
}
