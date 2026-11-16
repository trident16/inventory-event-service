package com.tarunnalla.inventory.item;

import com.tarunnalla.inventory.common.ConflictException;
import com.tarunnalla.inventory.common.NotFoundException;
import com.tarunnalla.inventory.events.StockChangedEvent;
import com.tarunnalla.inventory.events.StockEventPublisher;
import com.tarunnalla.inventory.item.dto.CreateItemRequest;
import com.tarunnalla.inventory.item.dto.ItemResponse;
import com.tarunnalla.inventory.item.dto.StockAdjustmentRequest;
import com.tarunnalla.inventory.item.dto.UpdateItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockEventPublisher eventPublisher;

    @InjectMocks
    private ItemService itemService;

    @Test
    void createSavesNewItem() {
        when(itemRepository.existsBySku("SKU-1")).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemResponse response = itemService.create(new CreateItemRequest("SKU-1", "Widget", 10, 2));

        assertThat(response.sku()).isEqualTo("SKU-1");
        assertThat(response.quantity()).isEqualTo(10);
        assertThat(response.lowStock()).isFalse();
    }

    @Test
    void createRejectsDuplicateSku() {
        when(itemRepository.existsBySku("SKU-1")).thenReturn(true);

        assertThatThrownBy(() -> itemService.create(new CreateItemRequest("SKU-1", "Widget", 10, 2)))
                .isInstanceOf(ConflictException.class);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(itemRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findById("missing"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("missing");
    }

    @Test
    void updateChangesNameAndThresholdButNotQuantity() {
        when(itemRepository.findById("abc")).thenReturn(Optional.of(item(10, 2)));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemResponse response = itemService.update("abc", new UpdateItemRequest("Widget Pro", 5));

        assertThat(response.name()).isEqualTo("Widget Pro");
        assertThat(response.lowStockThreshold()).isEqualTo(5);
        assertThat(response.quantity()).isEqualTo(10);
    }

    @Test
    void stockInIncreasesQuantityAndPublishesEvent() {
        when(itemRepository.findById("abc")).thenReturn(Optional.of(item(10, 2)));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemResponse response = itemService.adjustStock("abc",
                new StockAdjustmentRequest(AdjustmentType.IN, 5, "Supplier delivery"), "tarun");

        assertThat(response.quantity()).isEqualTo(15);

        ArgumentCaptor<StockChangedEvent> captor = ArgumentCaptor.forClass(StockChangedEvent.class);
        verify(eventPublisher).publish(captor.capture());
        StockChangedEvent event = captor.getValue();
        assertThat(event.eventId()).isNotBlank();
        assertThat(event.itemId()).isEqualTo("abc");
        assertThat(event.previousQuantity()).isEqualTo(10);
        assertThat(event.newQuantity()).isEqualTo(15);
        assertThat(event.performedBy()).isEqualTo("tarun");
        assertThat(event.reason()).isEqualTo("Supplier delivery");
    }

    @Test
    void stockOutBelowThresholdIsFlaggedAsLowStock() {
        when(itemRepository.findById("abc")).thenReturn(Optional.of(item(10, 2)));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemResponse response = itemService.adjustStock("abc",
                new StockAdjustmentRequest(AdjustmentType.OUT, 8, "Order #42"), "tarun");

        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.lowStock()).isTrue();
    }

    @Test
    void stockOutCannotGoNegativeAndPublishesNothing() {
        when(itemRepository.findById("abc")).thenReturn(Optional.of(item(3, 1)));

        assertThatThrownBy(() -> itemService.adjustStock("abc",
                new StockAdjustmentRequest(AdjustmentType.OUT, 5, null), "tarun"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("only 3 in stock");
        verify(itemRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    private static Item item(int quantity, int threshold) {
        Item item = new Item("SKU-1", "Widget", quantity, threshold);
        item.setId("abc");
        return item;
    }
}
