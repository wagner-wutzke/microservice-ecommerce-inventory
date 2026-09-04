package net.wowdev.ecommerce.inventory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.InventoryDTO;
import net.wowdev.ecommerce.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

class InventoryControllerTest {
  private InventoryService service;
  private InventoryController controller;
  private final UUID id = UUID.randomUUID();
  private final UUID productId = UUID.randomUUID();

  @BeforeEach void setUp() { service = mock(InventoryService.class); controller = new InventoryController(service); }

  @Test void delegatesReadsAndCreate() {
    InventoryDTO dto = new InventoryDTO(); dto.setId(id);
    when(service.findById(id, productId)).thenReturn(dto);
    when(service.findAll(productId, 2, 7)).thenReturn(new PageImpl<>(java.util.List.of(dto)));
    when(service.create(dto)).thenReturn(dto);
    assertThat(controller.findById(id, productId)).isSameAs(dto);
    assertThat(controller.findAll(productId, 2, 7).getContent()).containsExactly(dto);
    assertThat(controller.create(dto).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(controller.create(dto).getHeaders().getLocation().toString()).endsWith(id.toString());
  }

  @Test void deleteReturnsNoContent() {
    assertThat(controller.delete(id).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(service).delete(id);
  }
}
