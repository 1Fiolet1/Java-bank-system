package ru.seleznev.entities;

import jakarta.persistence.*;
import lombok.*;
import ru.seleznev.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "operations")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "account")
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal commission = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public Operation(OperationType type, BigDecimal amount, BigDecimal commission) {
        this.type = type;
        this.amount = amount;
        this.commission = commission;
        this.createdAt = LocalDateTime.now();
    }
}
