package net.wowdev.ecommerce.products.messaging;

import net.wowdev.ecommerce.domain.dto.InventoryDTO;
import net.wowdev.ecommerce.domain.entity.InventoryChangeType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class InventoryConsumerTest {
    @Test
    void consumesInventoryEvent() {
        final InventoryDTO inventory = new InventoryDTO(UUID.randomUUID(), null, null, 10, 2,
                InventoryChangeType.INVENTORY_INCREASE, null, null);

        new InventoryConsumer().consume(inventory);
    }
}
