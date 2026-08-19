package net.wowdev.ecommerce.products.controller;

import net.wowdev.ecommerce.domain.dto.ProductDTO;
import net.wowdev.ecommerce.products.service.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductControllerTest {
    private final ProductServiceImpl service = mock(ProductServiceImpl.class);
    private final ProductController controller = new ProductController(service);
    private final UUID id = UUID.randomUUID();
    private final Instant timestamp = Instant.parse("2026-01-01T00:00:00Z");
    private final ProductDTO request = new ProductDTO(id, "n", "d", 1.00d, "USD", "c", timestamp, timestamp);

    @Test void supportsCrudOperations() {
        final ProductDTO response = new ProductDTO(id, request.getName(), request.getDescription(), request.getUnitPrice(),
                request.getCurrency(), request.getCategory(), request.getCreatedAt(), request.getModifiedAt());
        when(service.findById(id)).thenReturn(response);
        when(service.findAll(0, 20)).thenReturn(new PageImpl<>(List.of(response)));
        when(service.create(request)).thenReturn(response);
        when(service.update(id, request)).thenReturn(response);
        assertThat(controller.findById(id)).isEqualTo(response);
        assertThat(controller.findAll(0, 20)).hasSize(1);
        assertThat(controller.create(request).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(controller.update(id, request)).isEqualTo(response);
        assertThat(controller.delete(id).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete(id);
    }
}
