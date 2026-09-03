package net.wowdev.ecommerce.inventory.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
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
      "${app.kafka.customers-topic}"
    },
    containerFactory = "kafkaListenerContainerFactory")
public class InventoryConsumer {

  private final InventoryService inventoryService;

  @KafkaHandler
  public void consume(final OrderProcessingStartedEvent event) {
    log.debug(">>>> Processing OrderProcessingStartedEvent: {}", event.eventId());
    try {
      inventoryService.process(event.orderDTO());
    } catch (Exception e) {
      log.error("Error while processing OrderProcessingStartedEvent: {}", event.eventId(), e);
    }
  }
}
