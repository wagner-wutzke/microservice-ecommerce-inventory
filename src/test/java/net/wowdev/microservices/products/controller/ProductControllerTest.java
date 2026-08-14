package net.wowdev.microservices.products.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.UUID;
import net.wowdev.microservices.products.dto.ProductRequest;
import net.wowdev.microservices.products.dto.ProductResponse;
import net.wowdev.microservices.products.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import java.util.List;
import org.springframework.http.HttpStatus;

class ProductControllerTest {
    private final ProductService service = mock(ProductService.class);
    private final ProductController controller = new ProductController(service);
    private final UUID id = UUID.randomUUID();
    private final ProductRequest request = new ProductRequest(new BigDecimal("1.00"), "n", "d", "c");

    @Test void supportsCrudOperations() {
        final ProductResponse response = new ProductResponse(id, request.unitPrice(), request.name(), request.description(), request.category(), null, null);
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
