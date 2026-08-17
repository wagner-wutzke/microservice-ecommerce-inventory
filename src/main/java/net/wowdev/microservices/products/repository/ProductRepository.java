package net.wowdev.microservices.products.repository;

import java.util.UUID;
import net.wowdev.microservice.ecommerce.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
}
