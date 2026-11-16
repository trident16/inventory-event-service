package com.tarunnalla.inventory.item;

import com.tarunnalla.inventory.common.ConflictException;
import com.tarunnalla.inventory.common.NotFoundException;
import com.tarunnalla.inventory.item.dto.ItemResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Test
    void createReturns201() throws Exception {
        when(itemService.create(any())).thenReturn(sampleResponse(10));

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku":"SKU-1","name":"Widget","quantity":10,"lowStockThreshold":2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU-1"))
                .andExpect(jsonPath("$.lowStock").value(false));
    }

    @Test
    void createWithInvalidBodyReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku":"bad sku","name":"","quantity":-1,"lowStockThreshold":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.sku").exists())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.quantity").exists());
    }

    @Test
    void unknownItemReturns404() throws Exception {
        when(itemService.findById("nope")).thenThrow(new NotFoundException("Item not found: nope"));

        mockMvc.perform(get("/api/items/nope"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Item not found: nope"));
    }

    @Test
    void removingTooMuchStockReturns409() throws Exception {
        when(itemService.adjustStock(eq("abc"), any()))
                .thenThrow(new ConflictException("only 3 in stock"));

        mockMvc.perform(post("/api/items/abc/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"OUT","quantity":5}
                                """))
                .andExpect(status().isConflict());
    }

    private static ItemResponse sampleResponse(int quantity) {
        Instant now = Instant.now();
        return new ItemResponse("abc", "SKU-1", "Widget", quantity, 2, quantity <= 2, now, now);
    }
}
