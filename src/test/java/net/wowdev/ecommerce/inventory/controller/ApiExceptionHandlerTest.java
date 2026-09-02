package net.wowdev.ecommerce.inventory.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import net.wowdev.ecommerce.inventory.service.InventoryNotFoundException;
import net.wowdev.ecommerce.inventory.service.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ApiExceptionHandlerTest {
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test void createsNotFoundProblem() {
        final var problem = handler.notFound(new ProductNotFoundException(UUID.randomUUID()));
        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getType()).isNotNull();
    }

    @Test void createsInventoryNotFoundProblem() {
        final var problem = handler.inventoryNotFound(new InventoryNotFoundException(UUID.randomUUID()));
        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
