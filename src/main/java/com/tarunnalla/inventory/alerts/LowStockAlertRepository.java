package com.tarunnalla.inventory.alerts;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LowStockAlertRepository extends MongoRepository<LowStockAlert, String> {

    boolean existsByEventId(String eventId);

    List<LowStockAlert> findByAcknowledgedFalseOrderByCreatedAtDesc();

    List<LowStockAlert> findAllByOrderByCreatedAtDesc();
}
