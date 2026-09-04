package net.wowdev.ecommerce.inventory.messaging;

import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.events.InventoryUpdateFailedEvent;
import net.wowdev.ecommerce.domain.events.InventoryUpdatedEvent;
import net.wowdev.ecommerce.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

class InventoryProducerTest {
  @Test void publishesInventoryUpdatedEvent() {
    KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);
    InventoryProducer producer = producer(kafka);
    UUID id = UUID.randomUUID();
    InventoryUpdatedEvent event = new InventoryUpdatedEvent(id, "tx", new OrderDTO(), Instant.now(), InventoryService.ORIGIN_SERVICE);
    producer.publish(event);
    verify(kafka).send("inventory", id.toString(), event);
  }

  @Test void publishesInventoryUpdateFailedEvent() {
    KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);
    InventoryProducer producer = producer(kafka);
    UUID id = UUID.randomUUID();
    InventoryUpdateFailedEvent event = new InventoryUpdateFailedEvent(id, "tx", new OrderDTO(), "reason", Instant.now(), InventoryService.ORIGIN_SERVICE);
    producer.publish(event);
    verify(kafka).send("inventory", id.toString(), event);
  }

  private InventoryProducer producer(KafkaTemplate<String, Object> kafka) {
    InventoryProducer producer = new InventoryProducer(kafka);
    ReflectionTestUtils.setField(producer, "topic", "inventory");
    return producer;
  }
}
