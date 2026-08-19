package net.wowdev.microservices.products.service;

import net.wowdev.microservice.ecommerce.dto.ProductDTO;
import net.wowdev.microservice.ecommerce.entity.ProductEntity;
import net.wowdev.microservice.ecommerce.mapper.ProductMapper;
import net.wowdev.microservices.products.repository.ProductRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ProductServiceImpl(final ProductRepository repository,
                              final ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    @Override
    public ProductDTO findById(final UUID id) {
        return ProductMapper.toDto(repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id)));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductDTO> findAll(final int page, final int pageSize) {
        return repository.findAll(PageRequest.of(page, pageSize,
                        Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(ProductMapper::toDto);
    }

    @Transactional
    @Override
    public ProductDTO create(final ProductDTO productDTO) {
        ProductEntity entity = ProductMapper.toEntity(productDTO);
        entity.setCreatedAt(Instant.now());
        entity.setModifiedAt(Instant.now());
        entity.setId(UUID.randomUUID());
        final ProductEntity saved = repository.save(entity);
        eventPublisher.publishEvent(ProductMapper.toDto(saved));
        return ProductMapper.toDto(saved);
    }

    @Transactional
    @Override
    public ProductDTO update(final UUID id, final ProductDTO productDTO) {
        final ProductEntity product = repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.setUnitPrice(productDTO.getUnitPrice());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setCategory(productDTO.getCategory());
        product.setModifiedAt(Instant.now());
        final ProductEntity saved = repository.save(product);
        ProductDTO dto = ProductMapper.toDto(saved);
        eventPublisher.publishEvent(dto);
        return dto;
    }

    @Transactional
    @Override
    public void delete(final UUID id) {
        if (!repository.existsById(id)) throw new ProductNotFoundException(id);
        repository.deleteById(id);
    }
}
