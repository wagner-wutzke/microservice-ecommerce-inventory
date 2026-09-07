package net.wowdev.ecommerce.inventory.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.InventoryUpdateFailedEvent;
import net.wowdev.ecommerce.domain.events.InventoryUpdatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Value("${app.kafka.inventory-topic}")
  private String inventoryTopic;

  @Value("${app.kafka.orders-topic}")
  private String ordersTopic;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
  public void publish(final InventoryUpdatedEvent event) {
    log.debug(
        ">> Publishing InventoryUpdatedEvent {} on topic {}", event.eventId(), inventoryTopic);
    kafkaTemplate.send(inventoryTopic, event.eventId().toString(), event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
  public void publish(InventoryUpdateFailedEvent event) {
    log.debug(
        ">> Publishing InventoryUpdateFailedEvent {} on topic {}", event.eventId(), ordersTopic);
    kafkaTemplate.send(ordersTopic, event.eventId().toString(), event);
  }
}
