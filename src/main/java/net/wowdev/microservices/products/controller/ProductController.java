package net.wowdev.microservices.products.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.UUID;
import net.wowdev.microservices.products.dto.ProductRequest;
import net.wowdev.microservices.products.dto.ProductResponse;
import net.wowdev.microservices.products.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService service;
    public ProductController(final ProductService service) { this.service = service; }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable final UUID id) { return service.findById(id); }

    @GetMapping
    public Page<ProductResponse> findAll(@RequestParam(defaultValue = "0") @PositiveOrZero final int page,
                                         @RequestParam(defaultValue = "20") @Positive final int pageSize) {
        return service.findAll(page, pageSize);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody final ProductRequest request) {
        final ProductResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/products/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable final UUID id, @Valid @RequestBody final ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
