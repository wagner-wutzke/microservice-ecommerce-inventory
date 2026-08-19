package net.wowdev.microservices.products.service;

import net.wowdev.microservice.ecommerce.dto.InventoryDTO;
import net.wowdev.microservice.ecommerce.entity.InventoryEntity;
import net.wowdev.microservice.ecommerce.mapper.InventoryMapper;
import net.wowdev.microservices.products.repository.InventoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public InventoryServiceImpl(final InventoryRepository repository,
                                final ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    @Override
    public InventoryDTO findById(final UUID id, final UUID productId) {
        return InventoryMapper.toDto(repository.findByIdAndProductEntity_Id(id, productId)
                .orElseThrow(() -> new InventoryNotFoundException(id)));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<InventoryDTO> findAll(final UUID productId, final int page, final int pageSize) {
        return repository.findAllByProductEntity_Id(productId, PageRequest.of(page, pageSize,
                        Sort.by(Sort.Direction.DESC, "createdAt")))
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
        final InventoryDTO payload = InventoryMapper.toDto(saved);
        eventPublisher.publishEvent(payload);
        return payload;
    }

    @Transactional
    @Override
    public void delete(final UUID id) {
        if (!repository.existsById(id)) {
            throw new InventoryNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
