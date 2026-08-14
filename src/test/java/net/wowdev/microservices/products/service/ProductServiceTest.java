package net.wowdev.microservices.products.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import net.wowdev.microservices.products.TestData;
import net.wowdev.microservices.products.domain.Product;
import net.wowdev.microservices.products.dto.ProductRequest;
import net.wowdev.microservices.products.mapper.ProductMapper;
import net.wowdev.microservices.products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock ProductRepository repository;
    @Mock ProductMapper mapper;
    @Mock ApplicationEventPublisher eventPublisher;
    private ProductService service;
    private final UUID id = TestData.product().getId();
    private final ProductRequest request = new ProductRequest(new BigDecimal("10.00"), "Name", "Description", "category");

    @BeforeEach void setUp() { service = new ProductService(repository, mapper, eventPublisher); }

    @Test void findsById() {
        when(repository.findById(id)).thenReturn(Optional.of(TestData.product()));
        assertThat(service.findById(id)).isNull();
        verify(mapper).toResponse(any(Product.class));
    }

    @Test void rejectsMissingId() {
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(id)).isInstanceOf(ProductNotFoundException.class);
    }

    @Test void listsProducts() {
        when(repository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(new PageImpl<>(java.util.List.of(TestData.product())));
        assertThat(service.findAll(0, 20)).hasSize(1);
    }

    @Test void createsAndPublishesAfterTransaction() {
        when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        service.create(request);
        verify(eventPublisher).publishEvent(any(ProductChangedEvent.class));
    }

    @Test void updatesAndDeletes() {
        final Product product = TestData.product();
        when(repository.findById(id)).thenReturn(Optional.of(product));
        when(repository.save(product)).thenReturn(product);
        service.update(id, request);
        verify(eventPublisher).publishEvent(any(ProductChangedEvent.class));
        when(repository.existsById(id)).thenReturn(true);
        service.delete(id);
        verify(repository).deleteById(id);
    }

    @Test void rejectsMissingUpdateAndDelete() {
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(id, request)).isInstanceOf(ProductNotFoundException.class);
        when(repository.existsById(id)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(ProductNotFoundException.class);
    }
}
