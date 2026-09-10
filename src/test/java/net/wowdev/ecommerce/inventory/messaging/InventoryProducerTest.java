package net.wowdev.ecommerce.inventory.messaging;

import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.events.InventoryCompleted;
import net.wowdev.ecommerce.domain.events.InventoryFailed;
import net.wowdev.ecommerce.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

class InventoryProducerTest {
  @Test
  void publishesInventoryCompletedEvent() {
    KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);
    InventoryProducer producer = producer(kafka);
    UUID id = UUID.randomUUID();
    InventoryCompleted event =
        new InventoryCompleted(
            id, "tx", new OrderDTO(), Instant.now(), InventoryService.ORIGIN_SERVICE);
    producer.publish(event);
    verify(kafka).send("inventory", id.toString(), event);
  }

  @Test
  void publishesInventoryUpdateFailedEvent() {
    KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);
    InventoryProducer producer = producer(kafka);
    UUID id = UUID.randomUUID();
    InventoryFailed event =
        new InventoryFailed(
            id, "tx", new OrderDTO(), "reason", Instant.now(), InventoryService.ORIGIN_SERVICE);
    producer.publish(event);
    verify(kafka).send("orders", id.toString(), event);
  }

  private InventoryProducer producer(KafkaTemplate<String, Object> kafka) {
    InventoryProducer producer = new InventoryProducer(kafka);
    ReflectionTestUtils.setField(producer, "inventoryTopic", "inventory");
    ReflectionTestUtils.setField(producer, "ordersTopic", "orders");
    return producer;
  }
}
