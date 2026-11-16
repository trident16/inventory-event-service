package com.tarunnalla.inventory.events;

import com.tarunnalla.inventory.item.AdjustmentType;

import java.time.Instant;

/**
 * Published to Kafka every time an item's stock level changes.
 *
 * @param eventId unique id, lets consumers ignore duplicates (Kafka delivers at-least-once)
 */
public record StockChangedEvent(
        String eventId,
        String itemId,
        String sku,
        AdjustmentType type,
        int quantityChanged,
        int previousQuantity,
        int newQuantity,
        int lowStockThreshold,
        String reason,
        String performedBy,
        Instant occurredAt
) {

    public boolean crossedLowStockThreshold() {
        return previousQuantity > lowStockThreshold && newQuantity <= lowStockThreshold;
    }
}
