package ru.seleznev.rates.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateGenerator {

    private final Map<String, BigDecimal> rates = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final double volatility;

    @Autowired
    public RateGenerator(@Value("${rates.volatility:0.02}") double volatility) {
        this.volatility = volatility;

        rates.put("USD", new BigDecimal("67.67"));
        rates.put("EUR", new BigDecimal("75.30"));
        rates.put("GBP", new BigDecimal("85.40"));
    }

    public void tick() {
        rates.replaceAll((currency, current) -> drift(current));
    }

    public Optional<BigDecimal> get(String currency) {
        return Optional.ofNullable(rates.get(currency));
    }

    public Map<String, BigDecimal> snapshot() {
        return Collections.unmodifiableMap(rates);
    }

    private BigDecimal drift(BigDecimal current) {
        double delta = (random.nextDouble() * 2 - 1) * volatility;
        BigDecimal factor = BigDecimal.ONE.add(BigDecimal.valueOf(delta));
        return current.multiply(factor).setScale(4, RoundingMode.HALF_UP);
    }

}
