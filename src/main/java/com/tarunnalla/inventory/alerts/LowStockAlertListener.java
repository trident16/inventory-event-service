package com.tarunnalla.inventory.alerts;

import com.tarunnalla.inventory.events.StockChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes stock events and raises an alert when an item drops to or below
 * its low-stock threshold.
 * <p>
 * Alerts are only raised when the threshold is <em>crossed</em>, so an item
 * that is already low doesn't produce a new alert on every sale. Processing
 * is idempotent: a redelivered event is detected by its event id.
 */
@Component
public class LowStockAlertListener {

    private static final Logger log = LoggerFactory.getLogger(LowStockAlertListener.class);

    private final LowStockAlertRepository alertRepository;

    public LowStockAlertListener(LowStockAlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @KafkaListener(topics = "${app.kafka.topics.stock-changed}", groupId = "low-stock-alerts")
    public void onStockChanged(StockChangedEvent event) {
        if (!event.crossedLowStockThreshold()) {
            return;
        }
        if (alertRepository.existsByEventId(event.eventId())) {
            log.debug("Ignoring duplicate event {}", event.eventId());
            return;
        }
        try {
            alertRepository.save(new LowStockAlert(event.eventId(), event.itemId(), event.sku(),
                    event.newQuantity(), event.lowStockThreshold(), event.occurredAt()));
            log.warn("Low stock: {} is at {} (threshold {})",
                    event.sku(), event.newQuantity(), event.lowStockThreshold());
        } catch (DuplicateKeyException ex) {
            // Another consumer instance saved the same event between our check and save.
            log.debug("Alert for event {} already exists", event.eventId());
        }
    }
}
