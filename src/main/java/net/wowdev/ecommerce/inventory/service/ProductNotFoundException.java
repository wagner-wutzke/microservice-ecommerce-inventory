package net.wowdev.ecommerce.inventory.service;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(final UUID id) { super("Product not found: " + id); }
}
