package com.tarunnalla.inventory.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void createIgnoresClientSuppliedId() {
        Item item = new Item("SKU-1", "Widget", 10, 2);
        item.setId("client-id");
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        Item saved = itemService.create(item);

        assertThat(saved.getId()).isNull();
        verify(itemRepository).save(item);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(itemRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findById("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Item not found");
    }

    @Test
    void updateCopiesFieldsOntoExistingItem() {
        Item existing = new Item("SKU-1", "Widget", 10, 2);
        existing.setId("abc");
        when(itemRepository.findById("abc")).thenReturn(Optional.of(existing));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        Item updated = itemService.update("abc", new Item("SKU-1", "Widget Pro", 25, 5));

        assertThat(updated.getId()).isEqualTo("abc");
        assertThat(updated.getName()).isEqualTo("Widget Pro");
        assertThat(updated.getQuantity()).isEqualTo(25);
        assertThat(updated.getLowStockThreshold()).isEqualTo(5);
    }
}
