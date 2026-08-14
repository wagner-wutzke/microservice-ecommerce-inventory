package net.wowdev.microservices.products.service;

import net.wowdev.microservices.products.domain.Product;

public record ProductChangedEvent(Product product) {
}
