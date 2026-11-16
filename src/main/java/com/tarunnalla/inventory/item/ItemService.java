package com.tarunnalla.inventory.item;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findById(String id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found: " + id));
    }

    public Item create(Item item) {
        item.setId(null);
        return itemRepository.save(item);
    }

    public Item update(String id, Item changes) {
        Item existing = findById(id);
        existing.setName(changes.getName());
        existing.setSku(changes.getSku());
        existing.setQuantity(changes.getQuantity());
        existing.setLowStockThreshold(changes.getLowStockThreshold());
        return itemRepository.save(existing);
    }

    public void delete(String id) {
        Item existing = findById(id);
        itemRepository.delete(existing);
    }
}
