package ru.seleznev.rates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.seleznev.dto.RateUpdate;
import ru.seleznev.services.RateCache;

@Component
public class RateListener {

    private final RateCache rateCache;

    @Autowired
    public RateListener(RateCache rateCache) {
        this.rateCache = rateCache;
    }

    @KafkaListener(
            topics = "${bank.kafka.topic.broadcast}",
            containerFactory = "rateUpdateListenerFactory"
    )
    public void onRateUpdate(RateUpdate update) {
        rateCache.put(update);
    }
}
