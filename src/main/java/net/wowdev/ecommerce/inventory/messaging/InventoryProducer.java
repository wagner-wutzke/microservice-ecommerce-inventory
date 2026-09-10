package net.wowdev.ecommerce.inventory.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.InventoryCompleted;
import net.wowdev.ecommerce.domain.events.InventoryFailed;
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

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void publish(final InventoryCompleted event) {
    log.debug(">> Publishing InventoryCompleted event {} on topic {}", event.eventId(), inventoryTopic);
    kafkaTemplate.send(inventoryTopic, event.eventId().toString(), event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void publish(InventoryFailed event) {
    log.debug(
        ">> Publishing InventoryFailed event {} on topic {}", event.eventId(), ordersTopic);
    kafkaTemplate.send(ordersTopic, event.eventId().toString(), event);
  }
}
