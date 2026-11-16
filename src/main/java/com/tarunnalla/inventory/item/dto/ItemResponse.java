package com.tarunnalla.inventory.item.dto;

import com.tarunnalla.inventory.item.Item;

import java.time.Instant;

public record ItemResponse(
        String id,
        String sku,
        String name,
        int quantity,
        int lowStockThreshold,
        boolean lowStock,
        Instant createdAt,
        Instant updatedAt
) {

    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getSku(),
                item.getName(),
                item.getQuantity(),
                item.getLowStockThreshold(),
                item.isLowStock(),
                item.getCreatedAt(),
                item.getUpdatedAt());
    }
}
