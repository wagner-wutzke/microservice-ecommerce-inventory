package net.wowdev.ecommerce.inventory.repository;

import java.util.UUID;
import net.wowdev.ecommerce.domain.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
}
