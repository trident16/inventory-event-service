package com.tarunnalla.inventory.alerts;

import com.tarunnalla.inventory.events.StockChangedEvent;
import com.tarunnalla.inventory.item.AdjustmentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LowStockAlertListenerTest {

    @Mock
    private LowStockAlertRepository alertRepository;

    @InjectMocks
    private LowStockAlertListener listener;

    @Test
    void createsAlertWhenStockCrossesThreshold() {
        when(alertRepository.existsByEventId("evt-1")).thenReturn(false);

        listener.onStockChanged(event("evt-1", 5, 2, 3));

        ArgumentCaptor<LowStockAlert> captor = ArgumentCaptor.forClass(LowStockAlert.class);
        verify(alertRepository).save(captor.capture());
        assertThat(captor.getValue().getSku()).isEqualTo("SKU-1");
        assertThat(captor.getValue().getQuantity()).isEqualTo(2);
        assertThat(captor.getValue().getThreshold()).isEqualTo(3);
    }

    @Test
    void ignoresChangesThatStayAboveThreshold() {
        listener.onStockChanged(event("evt-2", 20, 15, 3));

        verify(alertRepository, never()).save(any());
    }

    @Test
    void doesNotAlertAgainWhenAlreadyBelowThreshold() {
        listener.onStockChanged(event("evt-3", 2, 1, 3));

        verify(alertRepository, never()).save(any());
    }

    @Test
    void ignoresRedeliveredEvent() {
        when(alertRepository.existsByEventId("evt-1")).thenReturn(true);

        listener.onStockChanged(event("evt-1", 5, 2, 3));

        verify(alertRepository, never()).save(any());
    }

    private static StockChangedEvent event(String eventId, int before, int after, int threshold) {
        return new StockChangedEvent(eventId, "item-1", "SKU-1", AdjustmentType.OUT, before - after,
                before, after, threshold, "Order", "tarun", Instant.now());
    }
}
