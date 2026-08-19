package net.wowdev.microservices.products.messaging;

import net.wowdev.microservice.ecommerce.dto.InventoryDTO;
import net.wowdev.microservice.ecommerce.entity.InventoryChangeType;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InventoryMessagingTest {
    @Test
    void publishesInventoryPayload() {
        final KafkaTemplate<String, InventoryDTO> template = mock(KafkaTemplate.class);
        final InventoryDTO payload = new InventoryDTO(UUID.randomUUID(), null, null, 10, 2,
                InventoryChangeType.INVENTORY_INCREASE, null, null);

        new InventoryProducer(template, "inventory-events-topic").publish(payload);

        verify(template).send("inventory-events-topic", payload.getId().toString(), payload);
    }
}
