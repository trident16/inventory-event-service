package com.tarunnalla.inventory.events;

import com.tarunnalla.inventory.alerts.LowStockAlert;
import com.tarunnalla.inventory.alerts.LowStockAlertListener;
import com.tarunnalla.inventory.alerts.LowStockAlertRepository;
import com.tarunnalla.inventory.item.AdjustmentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * Publishes a real event to an in-memory Kafka broker and checks that the
 * listener receives it, deserialises it and raises an alert. MongoDB is
 * mocked so the test only needs Kafka.
 */
@SpringBootTest(classes = {StockEventPublisher.class, LowStockAlertListener.class, KafkaTopicConfig.class})
@ImportAutoConfiguration(KafkaAutoConfiguration.class)
@EmbeddedKafka(partitions = 1, topics = "inventory.stock-changed",
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
class StockEventKafkaIntegrationTest {

    @Autowired
    private StockEventPublisher publisher;

    @MockitoBean
    private LowStockAlertRepository alertRepository;

    @Test
    void publishedLowStockEventIsConsumedAndRaisesAlert() {
        String eventId = UUID.randomUUID().toString();

        publisher.publish(new StockChangedEvent(eventId, "item-1", "SKU-1", AdjustmentType.OUT, 8,
                10, 2, 3, "Order #42", "tarun", Instant.now()));

        verify(alertRepository, timeout(15_000)).save(argThat((LowStockAlert alert) ->
                alert.getEventId().equals(eventId) && alert.getQuantity() == 2));
    }
}
