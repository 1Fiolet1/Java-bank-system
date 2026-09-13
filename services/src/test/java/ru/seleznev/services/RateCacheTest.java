package ru.seleznev.services;

import org.junit.jupiter.api.Test;
import ru.seleznev.dto.RateUpdate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateCacheTest {

    private final RateCache rateCache = new RateCache();

    @Test
    void getReturnsEmptyWhenRateIsMissing() {
        Optional<RateUpdate> result = rateCache.get("USD");

        assertTrue(result.isEmpty());
    }

    @Test
    void putStoresRateByCurrencyCode() {
        RateUpdate update = new RateUpdate("USD", new BigDecimal("90.00"), Instant.now());

        rateCache.put(update);

        Optional<RateUpdate> result = rateCache.get("USD");
        assertTrue(result.isPresent());
        assertSame(update, result.get());
    }
}
