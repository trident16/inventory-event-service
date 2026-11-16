package com.tarunnalla.inventory.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Sends {@link StockChangedEvent}s to Kafka.
 * <p>
 * The item id is used as the message key, so all events for the same item
 * land on the same partition and are consumed in the order they happened.
 */
@Component
public class StockEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(StockEventPublisher.class);

    private final KafkaTemplate<String, StockChangedEvent> kafkaTemplate;
    private final String topic;

    public StockEventPublisher(KafkaTemplate<String, StockChangedEvent> kafkaTemplate,
                               @Value("${app.kafka.topics.stock-changed}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(StockChangedEvent event) {
        kafkaTemplate.send(topic, event.itemId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish stock event {} for {}", event.eventId(), event.sku(), ex);
                    } else {
                        log.debug("Published stock event {} to partition {}",
                                event.eventId(), result.getRecordMetadata().partition());
                    }
                });
    }
}
