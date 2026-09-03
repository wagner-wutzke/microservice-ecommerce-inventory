package net.wowdev.ecommerce.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@EntityScan(basePackages = "net.wowdev.ecommerce.domain.entity")
@SpringBootApplication(
    scanBasePackages = {"net.wowdev.ecommerce.inventory", "net.wowdev.ecommerce.datareplication"})
public class InventoryServiceApplication {
  public static void main(final String[] args) {
    SpringApplication.run(InventoryServiceApplication.class, args);
  }
}
