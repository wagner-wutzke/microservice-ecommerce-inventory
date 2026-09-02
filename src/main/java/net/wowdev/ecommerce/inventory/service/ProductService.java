package net.wowdev.ecommerce.inventory.service;

import net.wowdev.ecommerce.domain.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ProductService {

    ProductDTO findById(UUID id);

    @Transactional(readOnly = true)
    Page<ProductDTO> findAll(int page, int pageSize);

    @Transactional
    ProductDTO create(ProductDTO productDTO);

    @Transactional
    ProductDTO update(UUID id, ProductDTO productDTO);

    @Transactional
    void delete(UUID id);
}
