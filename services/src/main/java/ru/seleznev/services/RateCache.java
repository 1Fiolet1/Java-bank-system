package ru.seleznev.services;

import org.springframework.stereotype.Component;
import ru.seleznev.dto.RateUpdate;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateCache {

    private final ConcurrentHashMap<String, RateUpdate> rates = new ConcurrentHashMap<>();

    public void put(RateUpdate update) {
        rates.put(update.getCurrency(), update);
    }

    public Optional<RateUpdate> get(String currency) {
        return Optional.ofNullable(rates.get(currency));
    }
}
