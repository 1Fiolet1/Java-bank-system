package ru.seleznev.rates;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.seleznev.dto.RateRequest;
import ru.seleznev.dto.RateUpdate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaRequestReplyConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${bank.kafka.topic.reply}")
    private String replyTopic;

    @Bean
    public ProducerFactory<String, RateRequest> rateRequestProducerFactory(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                new JsonSerializer<>(objectMapper)
        );
    }

    @Bean
    public ConsumerFactory<String, RateUpdate> rateReplyConsumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bank-service-replies");

        JsonDeserializer<RateUpdate> deserializer = new JsonDeserializer<>(
                RateUpdate.class, objectMapper, false
        );
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public KafkaMessageListenerContainer<String, RateUpdate> rateReplyContainer(
            @Qualifier("rateReplyConsumerFactory") ConsumerFactory<String, RateUpdate> consumerFactory
    ) {
        KafkaMessageListenerContainer<String, RateUpdate> container =
                new KafkaMessageListenerContainer<>(
                        consumerFactory,
                        new ContainerProperties(replyTopic)
                );
        container.setAutoStartup(false);
        return container;
    }

    @Bean
    public ReplyingKafkaTemplate<String, RateRequest, RateUpdate> replyingKafkaTemplate(
            ProducerFactory<String, RateRequest> producerFactory,
            KafkaMessageListenerContainer<String, RateUpdate> replyContainer
    ) {
        return new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
    }
}
