package net.wowdev.microservices.products;

import net.wowdev.ecommerce.domain.dto.ProductDTO;

import java.time.Instant;
import java.util.UUID;

public final class TestData {
    private TestData() { }

    public static ProductDTO product() {
        final UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final Instant timestamp = Instant.parse("2026-01-01T00:00:00Z");
        return new ProductDTO(id, "Keyboard", "Mechanical keyboard", 12.34d, "USD", "hardware",
                timestamp, timestamp);
    }
}
