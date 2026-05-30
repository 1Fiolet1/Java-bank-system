package ru.seleznev.rates.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.seleznev.dto.RateUpdate;
import ru.seleznev.rates.generator.RateGenerator;

import java.time.Instant;

@Component
public class RatePublisher {

    private final RateGenerator rateGenerator;
    private final KafkaTemplate<String, RateUpdate> kafkaTemplate;

    @Value("${rates.kafka.topic.broadcast}")
    private String topic;

    @Autowired
    public RatePublisher(RateGenerator rateGenerator, KafkaTemplate<String, RateUpdate> kafkaTemplate) {
        this.rateGenerator = rateGenerator;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRateString = "${rates.publish-interval-ms}")
    public void publish() {
        rateGenerator.tick();
        Instant now = Instant.now();

        rateGenerator.snapshot().forEach((currency, rate) -> {
            RateUpdate update = new RateUpdate(currency, rate, now);
            kafkaTemplate.send(topic, currency, update);
        });
    }
}
