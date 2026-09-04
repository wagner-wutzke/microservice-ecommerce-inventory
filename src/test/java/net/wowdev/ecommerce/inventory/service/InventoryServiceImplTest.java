package net.wowdev.ecommerce.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.*;
import net.wowdev.ecommerce.domain.entity.InventoryEntity;
import net.wowdev.ecommerce.domain.enums.InventoryChangeType;
import net.wowdev.ecommerce.domain.events.*;
import net.wowdev.ecommerce.inventory.messaging.InventoryProducer;
import net.wowdev.ecommerce.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

class InventoryServiceImplTest {
  private InventoryRepository repository;
  private InventoryProducer producer;
  private InventoryServiceImpl service;
  private final UUID id = UUID.randomUUID();
  private final UUID productId = UUID.randomUUID();

  @BeforeEach void setUp() {
    repository = mock(InventoryRepository.class);
    producer = mock(InventoryProducer.class);
    service = new InventoryServiceImpl(repository, producer);
  }

  private InventoryEntity entity() {
    return new InventoryEntity(id, productId, UUID.randomUUID(), 8, 2, 6,
        InventoryChangeType.INVENTORY_INCREASE, Instant.now(), Instant.now());
  }

  private OrderDTO order() {
    OrderDTO order = new OrderDTO();
    order.setId(UUID.randomUUID());
    OrderLineDTO line = new OrderLineDTO();
    line.setProductId(productId);
    line.setQuantity(2);
    order.setOrderLines(java.util.List.of(line));
    return order;
  }

  @Test void findsProductScopedInventory() {
    when(repository.findByIdAndProductId(id, productId)).thenReturn(java.util.Optional.of(entity()));
    assertThat(service.findById(id, productId).getProductId()).isEqualTo(productId);
  }

  @Test void missingInventoryThrows() {
    when(repository.findByIdAndProductId(id, productId)).thenReturn(java.util.Optional.empty());
    assertThatThrownBy(() -> service.findById(id, productId)).isInstanceOf(InventoryNotFoundException.class);
  }

  @Test void findsPageWithDescendingCreationSort() {
    when(repository.findAllByProductId(eq(productId), any(Pageable.class)))
        .thenReturn(new PageImpl<>(java.util.List.of(entity())));
    assertThat(service.findAll(productId, 0, 10)).hasSize(1);
    verify(repository).findAllByProductId(eq(productId), argThat(p -> p.getPageSize() == 10
        && p.getSort().getOrderFor("createdAt").isDescending()));
  }

  @Test void createsInventoryWithGeneratedIdentityAndTimestamps() {
    InventoryEntity saved = entity();
    when(repository.save(any())).thenReturn(saved);
    InventoryDTO input = new InventoryDTO(null, productId, UUID.randomUUID(), 8, 2, 6,
        InventoryChangeType.INVENTORY_INCREASE, null, null);
    assertThat(service.create(input).getId()).isEqualTo(id);
    verify(repository).save(argThat(e -> e.getId() != null && e.getCreatedAt() != null && e.getModifiedAt() != null));
  }

  @Test void deletesExistingAndRejectsMissing() {
    when(repository.existsById(id)).thenReturn(true);
    service.delete(id);
    verify(repository).deleteById(id);
    when(repository.existsById(id)).thenReturn(false);
    assertThatThrownBy(() -> service.delete(id)).isInstanceOf(InventoryNotFoundException.class);
  }

  @Test void processesOrderAndPublishesUpdateEvent() {
    OrderDTO order = order();
    service.process(order);
    verify(producer).publish(argThat((InventoryUpdatedEvent e) -> e.orderDTO().equals(order)
        && e.origin().equals(InventoryService.ORIGIN_SERVICE)));
  }

  @Test void compensatesOrderWithReasonAndPublishesFailureEvent() {
    OrderDTO order = order();
    service.compensate(order, "payment failed");
    verify(producer).publish(argThat((InventoryUpdateFailedEvent e) -> e.orderDTO().equals(order)
        && e.reason().equals("payment failed") && e.origin().equals(InventoryService.ORIGIN_SERVICE)));
  }
}
