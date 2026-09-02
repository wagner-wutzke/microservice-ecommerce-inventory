package net.wowdev.ecommerce.inventory.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import net.wowdev.ecommerce.domain.dto.InventoryDTO;
import net.wowdev.ecommerce.inventory.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(final InventoryService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public InventoryDTO findById(@PathVariable final UUID id,
                                 @RequestParam final UUID productId) {
        return service.findById(id, productId);
    }

    @GetMapping
    public Page<InventoryDTO> findAll(@RequestParam final UUID productId,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero final int page,
                                      @RequestParam(defaultValue = "20") @Positive final int pageSize) {
        return service.findAll(productId, page, pageSize);
    }

    @PostMapping
    public ResponseEntity<InventoryDTO> create(@Valid @RequestBody final InventoryDTO inventoryDTO) {
        final InventoryDTO response = service.create(inventoryDTO);
        return ResponseEntity.created(URI.create("/api/v1/inventory/" + response.getId())).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
