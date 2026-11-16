package com.tarunnalla.inventory.item;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ItemRepository extends MongoRepository<Item, String> {

    Optional<Item> findBySku(String sku);

    boolean existsBySku(String sku);
}
