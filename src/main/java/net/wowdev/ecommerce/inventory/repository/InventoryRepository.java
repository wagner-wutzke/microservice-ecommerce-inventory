package net.wowdev.ecommerce.inventory.repository;

import java.util.Optional;
import java.util.UUID;
import net.wowdev.ecommerce.domain.entity.InventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, UUID> {
    Optional<InventoryEntity> findByIdAndProductId(UUID id, UUID productId);

    Page<InventoryEntity> findAllByProductId(UUID productId, Pageable pageable);
}
