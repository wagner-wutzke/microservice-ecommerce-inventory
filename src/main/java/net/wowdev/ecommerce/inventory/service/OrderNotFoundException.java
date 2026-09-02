package net.wowdev.ecommerce.inventory.service;

public class OrderNotFoundException extends RuntimeException {
  public OrderNotFoundException(final String message) {
    super(message);
  }
}
