package net.wowdev.ecommerce.inventory.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import net.wowdev.ecommerce.domain.events.PaymentFailedEvent;
import net.wowdev.ecommerce.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = {
      "${app.kafka.orders-topic}",
      "${app.kafka.payments-topic}"
    },
    containerFactory = "kafkaListenerContainerFactory")
public class InventoryConsumer {

  private final InventoryService inventoryService;

  @KafkaHandler
  public void consume(final OrderProcessingStartedEvent event) {
    log.debug(
        ">> Processing OrderProcessingStartedEvent sent by {}. Event id: {}",
        event.origin(),
        event.eventId());
    inventoryService.process(event.orderDTO());
  }

  @KafkaHandler
  public void consume(final PaymentFailedEvent event) {
    log.debug(
        ">> Processing PaymentFailedEvent sent by {}. Event id: {}",
        event.origin(),
        event.eventId());
    inventoryService.compensate(event.orderDTO(), event.reason());
  }
}
