package ru.seleznev.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;
import ru.seleznev.dto.RateRequest;
import ru.seleznev.dto.RateUpdate;
import ru.seleznev.exceptions.RateNotAvailableException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class RateExchangeService {

    private final RateCache cache;
    private final ReplyingKafkaTemplate<String, RateRequest, RateUpdate> replyingTemplate;

    @Value("${bank.kafka.topic.request}")
    private String requestTopic;

    @Value("${bank.rates.staleness-threshold-ms}")
    private long stalenessThresholdMs;

    @Value("${bank.rates.request-timeout-ms}")
    private long requestTimeoutMs;

    @Autowired
    public RateExchangeService(RateCache cache, ReplyingKafkaTemplate<String, RateRequest, RateUpdate> replyingTemplate) {
        this.cache = cache;
        this.replyingTemplate = replyingTemplate;
    }

    public RateUpdate getRate(String currency) {
        validateCurrency(currency);
        String normalized = currency.toUpperCase();

        if ("RUB".equals(normalized)) {
            return new RateUpdate("RUB", BigDecimal.ONE, Instant.now());
        }

        Optional<RateUpdate> cached = cache.get(normalized);
        if (cached.isPresent() && isFresh(cached.get())) {
            return cached.get();
        }

        RateUpdate fresh = requestFromService(normalized);
        if (fresh.getRateToRub() == null) {
            throw new RateNotAvailableException("Rate for currency " + normalized + " is not available");
        }

        cache.put(fresh);
        return fresh;
    }


    private boolean isFresh(RateUpdate rate) {
        return Duration.between(rate.getTimestamp(), Instant.now()).toMillis() < stalenessThresholdMs;
    }

    private RateUpdate requestFromService(String currency) {
        RateRequest request = new RateRequest(currency);
        ProducerRecord<String, RateRequest> record = new ProducerRecord<>(requestTopic, request);

        try {
            RequestReplyFuture<String, RateRequest, RateUpdate> future =
                    replyingTemplate.sendAndReceive(record);
            ConsumerRecord<String, RateUpdate> reply =
                    future.get(requestTimeoutMs, TimeUnit.MILLISECONDS);
            return reply.value();
        } catch (Exception e) {
            throw new RateNotAvailableException("Rate service is unavailable for currency " + currency);
        }
    }

    private void validateCurrency(String currency) {
        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException("Currency code must be a 3-letter code");
        }
    }
}
