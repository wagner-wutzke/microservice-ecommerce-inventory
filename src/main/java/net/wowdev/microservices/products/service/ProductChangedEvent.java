package net.wowdev.microservices.products.service;

import net.wowdev.microservice.ecommerce.dto.ProductDTO;

public record ProductChangedEvent(ProductDTO productDTO) {
}
