package net.wowdev.ecommerce.products.service;

import net.wowdev.ecommerce.domain.dto.InventoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface InventoryService {

    InventoryDTO findById(UUID id, UUID productId);

    @Transactional(readOnly = true)
    Page<InventoryDTO> findAll(UUID productId, int page, int pageSize);

    @Transactional
    InventoryDTO create(InventoryDTO inventoryDTO);

    @Transactional
    void delete(UUID id);
}
