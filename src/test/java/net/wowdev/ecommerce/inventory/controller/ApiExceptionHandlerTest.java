package net.wowdev.ecommerce.inventory.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import net.wowdev.ecommerce.inventory.service.InventoryNotFoundException;
import net.wowdev.ecommerce.inventory.service.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

    @Test void createsBadRequestProblemWithFieldErrors() {
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(
                new FieldError("request", "name", "must not be blank")));
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);
        Mockito.when(exception.getBindingResult()).thenReturn(bindingResult);
        final var problem = handler.invalid(exception);
        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getDetail()).isEqualTo("name: must not be blank");
    }
}
