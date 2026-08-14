package net.wowdev.microservices.products;

import java.math.BigDecimal;
import java.util.UUID;
import net.wowdev.microservices.products.domain.Product;

public final class TestData {
    private TestData() { }

    public static Product product() {
        final Product product = new Product(new BigDecimal("12.34"), "Keyboard", "Mechanical keyboard", "hardware");
        try {
            final var field = Product.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(product, UUID.fromString("11111111-1111-1111-1111-111111111111"));
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
        return product;
    }
}
