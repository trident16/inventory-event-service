package com.tarunnalla.inventory.item;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * A product kept in the warehouse, identified by a unique SKU.
 * <p>
 * {@code version} enables optimistic locking, so two concurrent stock
 * adjustments on the same item cannot silently overwrite each other.
 */
@Document(collection = "items")
public class Item {

    @Id
    private String id;

    @Indexed(unique = true)
    private String sku;

    private String name;

    private int quantity;

    private int lowStockThreshold;

    @Version
    private Long version;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Item() {
    }

    public Item(String sku, String name, int quantity, int lowStockThreshold) {
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    public boolean isLowStock() {
        return quantity <= lowStockThreshold;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
