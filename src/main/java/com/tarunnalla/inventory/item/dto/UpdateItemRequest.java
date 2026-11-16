package com.tarunnalla.inventory.item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Updates descriptive fields only. Quantity is deliberately not editable here:
 * stock changes must go through the stock adjustment endpoint so every change
 * is recorded with a type and reason.
 */
public record UpdateItemRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Min(0)
        int lowStockThreshold
) {
}
