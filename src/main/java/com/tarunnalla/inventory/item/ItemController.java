package com.tarunnalla.inventory.item;

import com.tarunnalla.inventory.item.dto.CreateItemRequest;
import com.tarunnalla.inventory.item.dto.ItemResponse;
import com.tarunnalla.inventory.item.dto.StockAdjustmentRequest;
import com.tarunnalla.inventory.item.dto.UpdateItemRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponse> list() {
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable String id) {
        return itemService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse create(@Valid @RequestBody CreateItemRequest request) {
        return itemService.create(request);
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable String id, @Valid @RequestBody UpdateItemRequest request) {
        return itemService.update(id, request);
    }

    @PostMapping("/{id}/stock")
    public ItemResponse adjustStock(@PathVariable String id,
                                    @Valid @RequestBody StockAdjustmentRequest request,
                                    Authentication authentication) {
        return itemService.adjustStock(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        itemService.delete(id);
    }
}
