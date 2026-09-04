package net.wowdev.ecommerce.inventory;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class InventoryServiceApplicationTest {
  @Test void mainBootstrapsSpringApplication() {
    new InventoryServiceApplication();
    try (MockedStatic<SpringApplication> spring = mockStatic(SpringApplication.class)) {
      InventoryServiceApplication.main(new String[] {"--test"});
      spring.verify(() -> SpringApplication.run(InventoryServiceApplication.class, new String[] {"--test"}));
    }
  }
}
