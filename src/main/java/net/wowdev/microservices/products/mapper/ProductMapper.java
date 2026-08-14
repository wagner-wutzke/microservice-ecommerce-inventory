package net.wowdev.microservices.products.mapper;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.UUID;
import net.wowdev.microservices.products.avro.Product;
import net.wowdev.microservices.products.dto.ProductRequest;
import net.wowdev.microservices.products.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public net.wowdev.microservices.products.domain.Product toEntity(final ProductRequest request) {
        return new net.wowdev.microservices.products.domain.Product(request.unitPrice(), request.name(), request.description(), request.category());
    }

    public ProductResponse toResponse(final net.wowdev.microservices.products.domain.Product product) {
        return new ProductResponse(product.getId(), product.getUnitPrice(), product.getName(), product.getDescription(),
                product.getCategory(), product.getCreatedAt(), product.getModifiedAt());
    }

    public Product toAvro(final net.wowdev.microservices.products.domain.Product product) {
        return Product.newBuilder().setId(product.getId()).setUnitPrice(decimalBytes(product.getUnitPrice()))
                .setName(product.getName()).setDescription(product.getDescription()).setCategory(product.getCategory()).build();
    }

    private ByteBuffer decimalBytes(final BigDecimal value) {
        return ByteBuffer.wrap(value.movePointRight(2).toBigIntegerExact().toByteArray());
    }

    public UUID toUuid(final Product event) {
        return event.getId();
    }
}
