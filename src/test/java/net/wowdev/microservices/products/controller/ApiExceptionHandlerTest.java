package net.wowdev.microservices.products.controller;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.UUID;
import net.wowdev.microservices.products.service.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ApiExceptionHandlerTest {
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test void createsNotFoundProblem() {
        final var problem = handler.notFound(new ProductNotFoundException(UUID.randomUUID()));
        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getType()).isNotNull();
    }
}
