package net.wowdev.microservices.products.service;

import java.util.UUID;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(final UUID id) {
        super("Inventory event not found: " + id);
    }
}
