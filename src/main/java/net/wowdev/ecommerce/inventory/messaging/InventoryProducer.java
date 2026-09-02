package net.wowdev.ecommerce.inventory.messaging;

import lombok.RequiredArgsConstructor;
import net.wowdev.ecommerce.domain.dto.InventoryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class InventoryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.inventory-events-topic}")
    private String topic;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(final InventoryDTO inventoryDTO) {
        kafkaTemplate.send(topic, inventoryDTO.getId().toString(), inventoryDTO);
    }
}
