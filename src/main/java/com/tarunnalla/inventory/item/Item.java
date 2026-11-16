package com.tarunnalla.inventory.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A product kept in the warehouse, identified by a unique SKU.
 */
@Document(collection = "items")
public class Item {

    @Id
    private String id;

    @NotBlank
    @Indexed(unique = true)
    private String sku;

    @NotBlank
    private String name;

    @Min(0)
    private int quantity;

    @Min(0)
    private int lowStockThreshold;

    public Item() {
    }

    public Item(String sku, String name, int quantity, int lowStockThreshold) {
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
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
}
