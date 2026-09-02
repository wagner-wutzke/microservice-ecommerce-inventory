package net.wowdev.ecommerce.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "net.wowdev.ecommerce.domain.entity")
public class InventoryServiceApplication {
    public static void main(final String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
