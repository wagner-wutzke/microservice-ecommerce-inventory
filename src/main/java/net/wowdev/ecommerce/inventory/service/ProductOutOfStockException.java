package net.wowdev.ecommerce.inventory.service;

import java.util.UUID;

public class ProductOutOfStockException extends RuntimeException {
  public ProductOutOfStockException(final UUID productId) {
    super("Product " + productId + " is out of stock.");
  }
}
