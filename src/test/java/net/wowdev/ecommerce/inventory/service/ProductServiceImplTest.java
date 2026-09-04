package net.wowdev.ecommerce.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.ProductDTO;
import net.wowdev.ecommerce.domain.entity.ProductEntity;
import net.wowdev.ecommerce.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class ProductServiceImplTest {
  private ProductRepository repository;
  private ApplicationEventPublisher publisher;
  private ProductServiceImpl service;
  private final UUID id = UUID.randomUUID();

  @BeforeEach void setUp() {
    repository = mock(ProductRepository.class);
    publisher = mock(ApplicationEventPublisher.class);
    service = new ProductServiceImpl(repository, publisher);
  }

  private ProductEntity entity() {
    return new ProductEntity(id, "Book", "A book", BigDecimal.TEN, "books", Instant.now(), Instant.now());
  }

  private ProductDTO dto() {
    return new ProductDTO(id, "Book", "A book", BigDecimal.TEN, "books", null, null);
  }

  @Test void findsProduct() {
    when(repository.findById(id)).thenReturn(java.util.Optional.of(entity()));
    assertThat(service.findById(id).getName()).isEqualTo("Book");
  }

  @Test void throwsWhenProductMissing() {
    when(repository.findById(id)).thenReturn(java.util.Optional.empty());
    assertThatThrownBy(() -> service.findById(id)).isInstanceOf(ProductNotFoundException.class);
  }

  @Test void findsPageWithNewestFirstOrdering() {
    when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(java.util.List.of(entity())));
    assertThat(service.findAll(1, 5).getContent()).hasSize(1);
    ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(pageable.capture());
    assertThat(pageable.getValue().getPageNumber()).isEqualTo(1);
    assertThat(pageable.getValue().getPageSize()).isEqualTo(5);
    assertThat(pageable.getValue().getSort().getOrderFor("createdAt").isDescending()).isTrue();
  }

  @Test void createsAssignsIdentityAndPublishesSavedDto() {
    ProductEntity saved = entity();
    when(repository.save(any(ProductEntity.class))).thenReturn(saved);
    ProductDTO result = service.create(dto());
    assertThat(result.getId()).isEqualTo(id);
    ArgumentCaptor<ProductEntity> captor = ArgumentCaptor.forClass(ProductEntity.class);
    verify(repository).save(captor.capture());
    assertThat(captor.getValue().getId()).isNotNull();
    assertThat(captor.getValue().getCreatedAt()).isNotNull();
    verify(publisher).publishEvent(result);
  }

  @Test void updatesFieldsAndPublishes() {
    ProductEntity product = entity();
    ProductDTO update = new ProductDTO(null, "New", "New description", BigDecimal.ONE, "new", null, null);
    when(repository.findById(id)).thenReturn(java.util.Optional.of(product));
    when(repository.save(product)).thenReturn(product);
    ProductDTO result = service.update(id, update);
    assertThat(result.getName()).isEqualTo("New");
    assertThat(product.getUnitPrice()).isEqualByComparingTo(BigDecimal.ONE);
    verify(publisher).publishEvent(result);
  }

  @Test void updateMissingThrows() {
    when(repository.findById(id)).thenReturn(java.util.Optional.empty());
    assertThatThrownBy(() -> service.update(id, dto())).isInstanceOf(ProductNotFoundException.class);
  }

  @Test void deletesExistingProduct() {
    when(repository.existsById(id)).thenReturn(true);
    service.delete(id);
    verify(repository).deleteById(id);
  }

  @Test void deleteMissingThrowsAndDoesNotDelete() {
    when(repository.existsById(id)).thenReturn(false);
    assertThatThrownBy(() -> service.delete(id)).isInstanceOf(ProductNotFoundException.class);
    verify(repository, never()).deleteById(any());
  }
}
