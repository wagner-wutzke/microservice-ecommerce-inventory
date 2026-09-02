package net.wowdev.ecommerce.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.dto.InventoryDTO;
import net.wowdev.ecommerce.domain.dto.OrderDTO;
import net.wowdev.ecommerce.domain.entity.InventoryEntity;
import net.wowdev.ecommerce.domain.mapper.InventoryMapper;
import net.wowdev.ecommerce.inventory.repository.InventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

  private final InventoryRepository repository;

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
  public void process(OrderDTO orderDTO) {
    // TODO check product availability for each order line. If available,
    //  create new row entry reducing amount from last product entries.
  }
}
