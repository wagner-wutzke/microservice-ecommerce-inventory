package net.wowdev.microservices.products.messaging;

import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.dto.InventoryDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InventoryConsumer {
    @KafkaListener(topics = "${app.kafka.inventory-changes-topic}", containerFactory = "productKafkaListenerContainerFactory")
    public void consume(final InventoryDTO inventoryDTO) {
        // The consumer is intentionally idempotent: downstream handling can be added without changing the contract.
        log.info("Received inventory event {}", inventoryDTO);
    }
}