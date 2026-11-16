package com.tarunnalla.inventory.item;

import com.tarunnalla.inventory.common.ConflictException;
import com.tarunnalla.inventory.common.NotFoundException;
import com.tarunnalla.inventory.events.StockChangedEvent;
import com.tarunnalla.inventory.events.StockEventPublisher;
import com.tarunnalla.inventory.item.dto.CreateItemRequest;
import com.tarunnalla.inventory.item.dto.ItemResponse;
import com.tarunnalla.inventory.item.dto.StockAdjustmentRequest;
import com.tarunnalla.inventory.item.dto.UpdateItemRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final StockEventPublisher eventPublisher;

    public ItemService(ItemRepository itemRepository, StockEventPublisher eventPublisher) {
        this.itemRepository = itemRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<ItemResponse> findAll() {
        return itemRepository.findAll().stream()
                .map(ItemResponse::from)
                .toList();
    }

    public ItemResponse findById(String id) {
        return ItemResponse.from(getItem(id));
    }

    public ItemResponse create(CreateItemRequest request) {
        if (itemRepository.existsBySku(request.sku())) {
            throw new ConflictException("An item with SKU " + request.sku() + " already exists");
        }
        Item item = new Item(request.sku(), request.name(), request.quantity(), request.lowStockThreshold());
        return ItemResponse.from(itemRepository.save(item));
    }

    public ItemResponse update(String id, UpdateItemRequest request) {
        Item item = getItem(id);
        item.setName(request.name());
        item.setLowStockThreshold(request.lowStockThreshold());
        return ItemResponse.from(itemRepository.save(item));
    }

    /**
     * Applies a stock movement, saves it, then publishes a {@link StockChangedEvent}.
     * The event is only sent after the database write succeeds.
     */
    public ItemResponse adjustStock(String id, StockAdjustmentRequest request, String performedBy) {
        Item item = getItem(id);
        int previousQuantity = item.getQuantity();
        int newQuantity = switch (request.type()) {
            case IN -> previousQuantity + request.quantity();
            case OUT -> previousQuantity - request.quantity();
        };
        if (newQuantity < 0) {
            throw new ConflictException("Cannot remove " + request.quantity() + " units of " + item.getSku()
                    + ": only " + previousQuantity + " in stock");
        }
        item.setQuantity(newQuantity);
        Item saved = itemRepository.save(item);

        eventPublisher.publish(new StockChangedEvent(
                UUID.randomUUID().toString(),
                saved.getId(),
                saved.getSku(),
                request.type(),
                request.quantity(),
                previousQuantity,
                newQuantity,
                saved.getLowStockThreshold(),
                request.reason(),
                performedBy,
                Instant.now()));

        return ItemResponse.from(saved);
    }

    public void delete(String id) {
        itemRepository.delete(getItem(id));
    }

    private Item getItem(String id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
    }
}
