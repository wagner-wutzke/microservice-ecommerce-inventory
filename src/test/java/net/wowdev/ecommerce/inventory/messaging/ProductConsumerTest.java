package net.wowdev.ecommerce.inventory.messaging;

import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.ProductDTO;
import org.junit.jupiter.api.Test;

class ProductConsumerTest {
  @Test void consumesProductChange() {
    ProductDTO product = new ProductDTO();
    product.setId(UUID.randomUUID());
    product.setName("item");
    new ProductConsumer().consume(product);
  }
}
