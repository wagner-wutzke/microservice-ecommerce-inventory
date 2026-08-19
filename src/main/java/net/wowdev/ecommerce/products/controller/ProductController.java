package net.wowdev.microservices.products.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import net.wowdev.ecommerce.domain.dto.ProductDTO;
import net.wowdev.microservices.products.service.ProductServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductServiceImpl service;

    public ProductController(final ProductServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ProductDTO findById(@PathVariable final UUID id) {
        return service.findById(id);
    }

    @GetMapping
    public Page<ProductDTO> findAll(@RequestParam(defaultValue = "0") @PositiveOrZero final int page,
                                    @RequestParam(defaultValue = "20") @Positive final int pageSize) {
        return service.findAll(page, pageSize);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody final ProductDTO productDTO) {
        final ProductDTO response = service.create(productDTO);
        return ResponseEntity.created(URI.create("/api/v1/products/" + response.getId())).body(response);
    }

    @PutMapping("/{id}")
    public ProductDTO update(@PathVariable final UUID id, @Valid @RequestBody final ProductDTO productDTO) {
        return service.update(id, productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
