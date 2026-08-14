package net.wowdev.microservices.products.repository;

import java.util.UUID;
import net.wowdev.microservices.products.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
