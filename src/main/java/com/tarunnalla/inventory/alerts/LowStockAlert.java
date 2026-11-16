package com.tarunnalla.inventory.alerts;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "low_stock_alerts")
public class LowStockAlert {

    @Id
    private String id;

    /** Id of the event that triggered the alert; unique so a redelivered event can't create a second alert. */
    @Indexed(unique = true)
    private String eventId;

    private String itemId;

    private String sku;

    private int quantity;

    private int threshold;

    private Instant createdAt;

    private boolean acknowledged;

    public LowStockAlert() {
    }

    public LowStockAlert(String eventId, String itemId, String sku, int quantity, int threshold, Instant createdAt) {
        this.eventId = eventId;
        this.itemId = itemId;
        this.sku = sku;
        this.quantity = quantity;
        this.threshold = threshold;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }
}
