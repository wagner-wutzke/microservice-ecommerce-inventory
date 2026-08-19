package net.wowdev.microservices.products.controller;

import net.wowdev.microservices.products.service.InventoryNotFoundException;
import net.wowdev.microservices.products.service.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
