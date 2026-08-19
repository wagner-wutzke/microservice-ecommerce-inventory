package net.wowdev.ecommerce.products.controller;

import net.wowdev.ecommerce.domain.dto.InventoryDTO;
import net.wowdev.ecommerce.domain.entity.InventoryChangeType;
import net.wowdev.ecommerce.products.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InventoryControllerTest {
    private final InventoryService service = mock(InventoryService.class);
    private final InventoryController controller = new InventoryController(service);
    private final UUID id = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();
    private final InventoryDTO inventory = new InventoryDTO(id, null, null, 10, 2,
            InventoryChangeType.INVENTORY_INCREASE, null, null);

    @Test
    void supportsReadAndAppendOperations() {
        when(service.findById(id, productId)).thenReturn(inventory);
        when(service.findAll(productId, 0, 20)).thenReturn(new PageImpl<>(List.of(inventory)));
        when(service.create(inventory)).thenReturn(inventory);

        assertThat(controller.findById(id, productId)).isEqualTo(inventory);
        assertThat(controller.findAll(productId, 0, 20)).hasSize(1);
        assertThat(controller.create(inventory).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(controller.delete(id).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete(id);
    }
}
