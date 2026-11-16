package com.tarunnalla.inventory.item.dto;

import com.tarunnalla.inventory.item.AdjustmentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StockAdjustmentRequest(
        @NotNull
        AdjustmentType type,

        @Min(1)
        int quantity,

        @Size(max = 200)
        String reason
) {
}
