package net.wowdev.ecommerce.inventory.service;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.dto.InventoryDTO;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.entity.InventoryEntity;
import net.wowdev.ecommerce.domain.events.InventoryCompleted;
import net.wowdev.ecommerce.domain.events.InventoryFailed;
import net.wowdev.ecommerce.domain.mapper.InventoryMapper;
import net.wowdev.ecommerce.inventory.messaging.InventoryProducer;
import net.wowdev.ecommerce.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

  private final InventoryRepository repository;

  private final InventoryProducer producer;

  @Value("${app.service.inventory.failing}")
  private boolean failsWhenRunning;

  @Transactional(readOnly = true)
  @Override
  public InventoryDTO findById(final UUID id, final UUID productId) {
    return InventoryMapper.toDto(
        repository
            .findByIdAndProductId(id, productId)
            .orElseThrow(() -> new InventoryNotFoundException(id)));
  }

  @Transactional(readOnly = true)
  @Override
  public Page<InventoryDTO> findAll(final UUID productId, final int page, final int pageSize) {
    return repository
        .findAllByProductId(
            productId, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")))
        .map(InventoryMapper::toDto);
  }

  @Transactional
  @Override
  public InventoryDTO create(final InventoryDTO inventoryDTO) {
    final InventoryEntity entity = InventoryMapper.toEntity(inventoryDTO);
    entity.setId(UUID.randomUUID());
    entity.setCreatedAt(Instant.now());
    entity.setModifiedAt(Instant.now());
    final InventoryEntity saved = repository.save(entity);
    return InventoryMapper.toDto(saved);
  }

  @Transactional
  @Override
  public void delete(final UUID id) {
    if (!repository.existsById(id)) {
      throw new InventoryNotFoundException(id);
    }
    repository.deleteById(id);
  }

  @Override
  @Transactional
  public void process(OrderDTO orderDTO) {
    // TODO check product availability for each order line. If available,
    //  create new row entry reducing amount from last product entries.
    try {
      if (failsWhenRunning) {
        throw new RuntimeException(
            "Product " + orderDTO.getOrderLines().getFirst().getProductId() + " is out of stock.");
      }
    } catch (RuntimeException e) {
      log.debug(
          ">> Inventory update for order {} failed. Reason: {}", orderDTO.getId(), e.getMessage());
    }

    log.debug(">> Processing inventory update for order: {}", orderDTO.getId());
    orderDTO
        .getOrderLines()
        .forEach(
            orderLine -> {
              // TODO update product inventory
              log.debug(">> Updating inventory for product: {}", orderLine.getProductId());
            });
    publishInventoryCompletedEvent(orderDTO);
  }

  @Override
  @Transactional
  public void compensate(OrderDTO orderDTO, final String reason) {
    orderDTO
        .getOrderLines()
        .forEach(
            orderLine -> {
              // TODO update product inventory
              log.debug(">> Compensating inventory for product: {}", orderLine.getProductId());
            });
    publishInventoryFailedEvent(orderDTO, reason);
  }

  private void publishInventoryCompletedEvent(OrderDTO orderDTO) {
    producer.publish(
        new InventoryCompleted(
            UUID.randomUUID(),
            orderDTO.getId().toString(),
            orderDTO,
            Instant.now(),
            InventoryService.ORIGIN_SERVICE));
  }

  private void publishInventoryFailedEvent(OrderDTO orderDTO, String reason) {
    producer.publish(
        new InventoryFailed(
            UUID.randomUUID(),
            orderDTO.getId().toString(),
            orderDTO,
            reason,
            Instant.now(),
            InventoryService.ORIGIN_SERVICE));
  }

  private boolean failsWhenRunning() {
    if (this.failsWhenRunning) {
      log.debug(
          """
          >> Service is configured to be failing when processing events. "
             This option can be configured with the "SERVICE_INVENTORY_FAILING" environment variable.
          """);
    }
    return this.failsWhenRunning;
  }
}
