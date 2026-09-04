package net.wowdev.ecommerce.inventory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.ProductDTO;
import net.wowdev.ecommerce.inventory.service.ProductServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

class ProductControllerTest {
  private ProductServiceImpl service;
  private ProductController controller;
  private final UUID id = UUID.randomUUID();

  @BeforeEach void setUp() { service = mock(ProductServiceImpl.class); controller = new ProductController(service); }

  @Test void delegatesAllEndpoints() {
    ProductDTO dto = new ProductDTO(); dto.setId(id);
    when(service.findById(id)).thenReturn(dto);
    when(service.findAll(1, 9)).thenReturn(new PageImpl<>(java.util.List.of(dto)));
    when(service.create(dto)).thenReturn(dto);
    when(service.update(id, dto)).thenReturn(dto);
    assertThat(controller.findById(id)).isSameAs(dto);
    assertThat(controller.findAll(1, 9).getTotalElements()).isOne();
    assertThat(controller.create(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(controller.update(id, dto)).isSameAs(dto);
    assertThat(controller.delete(id).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(service).delete(id);
  }
}
