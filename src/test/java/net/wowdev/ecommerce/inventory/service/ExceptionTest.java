package net.wowdev.ecommerce.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ExceptionTest {
  @Test void messagesIdentifyMissingResources() {
    UUID id = UUID.randomUUID();
    assertThat(new InventoryNotFoundException(id).getMessage()).contains(id.toString());
    assertThat(new ProductNotFoundException(id).getMessage()).contains(id.toString());
  }
}
