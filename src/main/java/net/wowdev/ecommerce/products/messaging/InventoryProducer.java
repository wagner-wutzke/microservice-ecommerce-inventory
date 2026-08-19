package net.wowdev.ecommerce.products.messaging;

import net.wowdev.ecommerce.domain.dto.InventoryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class InventoryProducer {
    private final KafkaTemplate<String, InventoryDTO> kafkaTemplate;
    private final String topic;

    public InventoryProducer(final KafkaTemplate<String, InventoryDTO> kafkaTemplate,
                              @Value("${app.kafka.inventory-changes-topic}") final String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(final InventoryDTO inventoryDTO) {
        kafkaTemplate.send(topic, inventoryDTO.getId().toString(), inventoryDTO);
    }
}
