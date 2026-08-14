package net.wowdev.microservices.products.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import net.wowdev.microservices.products.TestData;
import net.wowdev.microservices.products.dto.ProductRequest;
import org.junit.jupiter.api.Test;

class ProductMapperTest {
    private final ProductMapper mapper = new ProductMapper();

    @Test
    void mapsRequestResponseAndAvro() {
        final ProductRequest request = new ProductRequest(new BigDecimal("2.50"), "Mouse", "Wireless mouse", "hardware");
        final var entity = mapper.toEntity(request);
        assertThat(entity.getName()).isEqualTo("Mouse");
        assertThat(mapper.toResponse(TestData.product()).unitPrice()).isEqualByComparingTo("12.34");
        final var event = mapper.toAvro(TestData.product());
        assertThat(event.getId()).isEqualTo(TestData.product().getId());
        assertThat(mapper.toUuid(event)).isEqualTo(TestData.product().getId());
        assertThat(event.getUnitPrice()).isNotNull();
    }
}
