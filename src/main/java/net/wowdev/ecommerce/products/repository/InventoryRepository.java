package net.wowdev.microservices.products.repository;

import net.wowdev.ecommerce.domain.entity.InventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, UUID> {
    Optional<InventoryEntity> findByIdAndProductEntity_Id(UUID id, UUID productId);

    Page<InventoryEntity> findAllByProductEntity_Id(UUID productId, Pageable pageable);
}
