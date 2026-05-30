package ru.seleznev.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateUpdate {

    private String currency;

    private BigDecimal rateToRub;

    private Instant timestamp;
}
