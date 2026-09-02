package net.wowdev.ecommerce.inventory.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.events.OrderCreatedEvent;
import net.wowdev.ecommerce.domain.events.PaymentCompletedEvent;
import net.wowdev.ecommerce.inventory.service.OrderReplicationService;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = {
      "${app.kafka.order-events-topic}",
      "${app.kafka.payment-events-topic}"
    },
    containerFactory = "kafkaListenerContainerFactory")
public class InventoryConsumer {

  private final OrderReplicationService orderReplicationService;

  @KafkaHandler
  public void consume(final PaymentCompletedEvent event) {
    log.debug(">>>> Processing PaymentCompletedEvent...");
  }

  @KafkaHandler
  public void consume(final OrderCreatedEvent event) {
    log.debug(">>>> Processing OrderCreatedEvent...");
    orderReplicationService.replicate(event.orderDTO());
  }
}
