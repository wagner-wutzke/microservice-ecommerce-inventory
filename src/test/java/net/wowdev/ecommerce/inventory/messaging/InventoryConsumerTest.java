package net.wowdev.ecommerce.inventory.messaging;

import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.events.OrderProcessingStartedEvent;
import net.wowdev.ecommerce.domain.events.PaymentFailedEvent;
import net.wowdev.ecommerce.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;

class InventoryConsumerTest {
  @Test void processesOrderStartedEvent() {
    InventoryService service = mock(InventoryService.class);
    InventoryConsumer consumer = new InventoryConsumer(service);
    OrderDTO order = new OrderDTO();
    consumer.consume(new OrderProcessingStartedEvent(UUID.randomUUID(), "tx", order, Instant.now(), "orders"));
    verify(service).process(order);
  }

  @Test void compensatesPaymentFailureEvent() {
    InventoryService service = mock(InventoryService.class);
    InventoryConsumer consumer = new InventoryConsumer(service);
    OrderDTO order = new OrderDTO();
    consumer.consume(new PaymentFailedEvent(UUID.randomUUID(), "tx", order, "declined", Instant.now(), "payments"));
    verify(service).compensate(order, "declined");
  }

  @Test void acceptsUnknownEventWithoutDelegating() {
    InventoryService service = mock(InventoryService.class);
    new InventoryConsumer(service).handleUnknown("unknown");
    verifyNoInteractions(service);
  }
}
