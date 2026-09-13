package ru.seleznev.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import ru.seleznev.dto.RateRequest;
import ru.seleznev.dto.RateUpdate;
import ru.seleznev.exceptions.RateNotAvailableException;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateExchangeServiceTest {

    @Mock
    private RateCache cache;

    @Mock
    private ReplyingKafkaTemplate<String, RateRequest, RateUpdate> replyingTemplate;

    @Mock
    private RequestReplyFuture<String, RateRequest, RateUpdate> replyFuture;

    @InjectMocks
    private RateExchangeService rateExchangeService;

    @BeforeEach
    void setUp() {
        setField("requestTopic", "rates.request");
        setField("stalenessThresholdMs", 30_000L);
        setField("requestTimeoutMs", 5_000L);
    }

    @Test
    void getRateReturnsRubRateWithoutExternalRequest() {
        RateUpdate result = rateExchangeService.getRate("rub");

        assertEquals("RUB", result.getCurrency());
        assertBigDecimalEquals("1", result.getRateToRub());
        verify(cache, never()).get(any());
        verify(replyingTemplate, never()).sendAndReceive(any(ProducerRecord.class));
    }

    @Test
    void getRateReturnsFreshCachedRate() {
        RateUpdate cachedRate = new RateUpdate("USD", new BigDecimal("90.00"), Instant.now());
        when(cache.get("USD")).thenReturn(Optional.of(cachedRate));

        RateUpdate result = rateExchangeService.getRate("usd");

        assertSame(cachedRate, result);
        verify(replyingTemplate, never()).sendAndReceive(any(ProducerRecord.class));
    }

    @Test
    void getRateRequestsFreshRateWhenCacheIsMissing() throws Exception {
        RateUpdate freshRate = new RateUpdate("USD", new BigDecimal("91.25"), Instant.now());
        ConsumerRecord<String, RateUpdate> reply = new ConsumerRecord<>("rates.reply", 0, 0L, "USD", freshRate);

        when(cache.get("USD")).thenReturn(Optional.empty());
        when(replyingTemplate.sendAndReceive(any(ProducerRecord.class))).thenReturn(replyFuture);
        when(replyFuture.get(5_000L, TimeUnit.MILLISECONDS)).thenReturn(reply);

        RateUpdate result = rateExchangeService.getRate("usd");

        assertSame(freshRate, result);
        verify(cache).put(freshRate);

        ArgumentCaptor<ProducerRecord<String, RateRequest>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(replyingTemplate).sendAndReceive(recordCaptor.capture());

        ProducerRecord<String, RateRequest> record = recordCaptor.getValue();
        assertEquals("rates.request", record.topic());
        assertEquals("USD", record.value().getCurrency());
    }

    @Test
    void getRateThrowsWhenRequestedRateHasNoValue() throws Exception {
        RateUpdate unavailableRate = new RateUpdate("USD", null, Instant.now());
        ConsumerRecord<String, RateUpdate> reply = new ConsumerRecord<>("rates.reply", 0, 0L, "USD", unavailableRate);

        when(cache.get("USD")).thenReturn(Optional.empty());
        when(replyingTemplate.sendAndReceive(any(ProducerRecord.class))).thenReturn(replyFuture);
        when(replyFuture.get(5_000L, TimeUnit.MILLISECONDS)).thenReturn(reply);

        assertThrows(RateNotAvailableException.class, () -> rateExchangeService.getRate("usd"));

        verify(cache, never()).put(any());
    }

    @Test
    void getRateRejectsInvalidCurrencyCode() {
        assertThrows(IllegalArgumentException.class, () -> rateExchangeService.getRate("US"));
    }

    private void setField(String fieldName, Object value) {
        try {
            Field field = RateExchangeService.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(rateExchangeService, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
