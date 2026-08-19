package net.wowdev.ecommerce.products.service;

import net.wowdev.ecommerce.domain.dto.ProductDTO;
import net.wowdev.ecommerce.domain.entity.ProductEntity;
import net.wowdev.ecommerce.products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Mock private ProductRepository repository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductServiceImpl(repository, eventPublisher);
    }

    @Test
    void findsById() {
        when(repository.findById(ID)).thenReturn(Optional.of(product()));

        final ProductDTO result = service.findById(ID);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getName()).isEqualTo("Keyboard");
        assertThat(result.getUnitPrice()).isEqualTo(12.34d);
    }

    @Test
    void rejectsMissingId() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(ID))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void listsProducts() {
        when(repository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(product())));

        assertThat(service.findAll(0, 20).getContent())
                .singleElement()
                .extracting(ProductDTO::getId)
                .isEqualTo(ID);
    }

    @Test
    void createsAndPublishesProductEvent() {
        final ProductDTO request = dto("Name", 10.00d);
        when(repository.save(any(ProductEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final ProductDTO result = service.create(request);

        assertThat(result.getName()).isEqualTo("Name");
        verify(eventPublisher).publishEvent(result);
    }

    @Test
    void updatesAndDeletes() {
        final ProductEntity product = product();
        final ProductDTO request = dto("Updated name", 20.00d);
        when(repository.findById(ID)).thenReturn(Optional.of(product));
        when(repository.save(product)).thenReturn(product);
        when(repository.existsById(ID)).thenReturn(true);

        final ProductDTO result = service.update(ID, request);
        service.delete(ID);

        assertThat(product.getName()).isEqualTo("Updated name");
        assertThat(product.getUnitPrice()).isEqualTo(20.00d);
        assertThat(result.getName()).isEqualTo("Updated name");
        verify(eventPublisher).publishEvent(result);
        verify(repository).deleteById(ID);
    }

    @Test
    void rejectsMissingUpdateAndDelete() {
        when(repository.findById(ID)).thenReturn(Optional.empty());
        when(repository.existsById(ID)).thenReturn(false);
        final ProductDTO request = dto("Name", 10.00d);

        assertThatThrownBy(() -> service.update(ID, request))
                .isInstanceOf(ProductNotFoundException.class);
        assertThatThrownBy(() -> service.delete(ID))
                .isInstanceOf(ProductNotFoundException.class);
    }

    private static ProductEntity product() {
        return new ProductEntity(ID, "Keyboard", "Mechanical keyboard", 12.34d, "USD", "hardware",
                Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z"));
    }

    private static ProductDTO dto(final String name, final double unitPrice) {
        return new ProductDTO(ID, name, "Description", unitPrice, "USD", "hardware",
                Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z"));
    }
}
