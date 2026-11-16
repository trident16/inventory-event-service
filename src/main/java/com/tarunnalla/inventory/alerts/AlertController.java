package com.tarunnalla.inventory.alerts;

import com.tarunnalla.inventory.common.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final LowStockAlertRepository alertRepository;

    public AlertController(LowStockAlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    /** Open (unacknowledged) alerts by default; pass {@code ?all=true} for the full history. */
    @GetMapping
    public List<LowStockAlert> list(@RequestParam(defaultValue = "false") boolean all) {
        return all
                ? alertRepository.findAllByOrderByCreatedAtDesc()
                : alertRepository.findByAcknowledgedFalseOrderByCreatedAtDesc();
    }

    @PostMapping("/{id}/acknowledge")
    public LowStockAlert acknowledge(@PathVariable String id) {
        LowStockAlert alert = alertRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Alert not found: " + id));
        alert.setAcknowledged(true);
        return alertRepository.save(alert);
    }
}
