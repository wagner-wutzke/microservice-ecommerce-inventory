package net.wowdev.microservices.products.service;

import java.util.UUID;
import net.wowdev.microservices.products.domain.Product;
import net.wowdev.microservices.products.dto.ProductRequest;
import net.wowdev.microservices.products.dto.ProductResponse;
import net.wowdev.microservices.products.mapper.ProductMapper;
import net.wowdev.microservices.products.repository.ProductRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    public ProductService(final ProductRepository repository, final ProductMapper mapper,
                          final ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(final UUID id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id)));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(final int page, final int pageSize) {
        return repository.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"))).map(mapper::toResponse);
    }

    @Transactional
    public ProductResponse create(final ProductRequest request) {
        final Product saved = repository.save(new Product(request.unitPrice(), request.name(), request.description(), request.category()));
        eventPublisher.publishEvent(new ProductChangedEvent(saved));
        return mapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse update(final UUID id, final ProductRequest request) {
        final Product product = repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.update(request.unitPrice(), request.name(), request.description(), request.category());
        final Product saved = repository.save(product);
        eventPublisher.publishEvent(new ProductChangedEvent(saved));
        return mapper.toResponse(saved);
    }

    @Transactional
    public void delete(final UUID id) {
        if (!repository.existsById(id)) throw new ProductNotFoundException(id);
        repository.deleteById(id);
    }
}
