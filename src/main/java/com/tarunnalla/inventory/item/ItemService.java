package com.tarunnalla.inventory.item;

import com.tarunnalla.inventory.common.ConflictException;
import com.tarunnalla.inventory.common.NotFoundException;
import com.tarunnalla.inventory.item.dto.CreateItemRequest;
import com.tarunnalla.inventory.item.dto.ItemResponse;
import com.tarunnalla.inventory.item.dto.StockAdjustmentRequest;
import com.tarunnalla.inventory.item.dto.UpdateItemRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
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

    public ItemResponse adjustStock(String id, StockAdjustmentRequest request) {
        Item item = getItem(id);
        int newQuantity = switch (request.type()) {
            case IN -> item.getQuantity() + request.quantity();
            case OUT -> item.getQuantity() - request.quantity();
        };
        if (newQuantity < 0) {
            throw new ConflictException("Cannot remove " + request.quantity() + " units of " + item.getSku()
                    + ": only " + item.getQuantity() + " in stock");
        }
        item.setQuantity(newQuantity);
        return ItemResponse.from(itemRepository.save(item));
    }

    public void delete(String id) {
        itemRepository.delete(getItem(id));
    }

    private Item getItem(String id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
    }
}
