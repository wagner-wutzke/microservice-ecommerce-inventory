package net.wowdev.microservices.products.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(UUID id, BigDecimal unitPrice, String name, String description, String category,
                              Instant createdAt, Instant modifiedAt) {
}
