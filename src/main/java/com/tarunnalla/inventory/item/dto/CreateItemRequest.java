package com.tarunnalla.inventory.item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateItemRequest(
        @NotBlank
        @Pattern(regexp = "^[A-Z0-9-]{3,32}$", message = "must be 3-32 uppercase letters, digits or dashes")
        String sku,

        @NotBlank
        @Size(max = 100)
        String name,

        @Min(0)
        int quantity,

        @Min(0)
        int lowStockThreshold
) {
}
