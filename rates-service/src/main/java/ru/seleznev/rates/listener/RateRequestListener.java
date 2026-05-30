package ru.seleznev.rates.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import ru.seleznev.dto.RateRequest;
import ru.seleznev.dto.RateUpdate;
import ru.seleznev.rates.generator.RateGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Component
public class RateRequestListener {

    private final RateGenerator rateGenerator;

    @Autowired
    public RateRequestListener(RateGenerator rateGenerator) {
        this.rateGenerator = rateGenerator;
    }

    @KafkaListener(
            topics = "${rates.kafka.topic.request}",
            containerFactory = "rateRequestListenerFactory"
    )
    @SendTo
    public RateUpdate handleRequest(RateRequest request) {
        Optional<BigDecimal> rate = rateGenerator.get(request.getCurrency());

        return new RateUpdate(
                request.getCurrency(),
                rate.orElse(null),
                Instant.now()
        );
    }
}
