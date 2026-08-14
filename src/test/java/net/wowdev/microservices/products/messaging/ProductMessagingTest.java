package net.wowdev.microservices.products.messaging;

import static org.mockito.Mockito.*;
import net.wowdev.microservices.products.TestData;
import net.wowdev.microservices.products.avro.Product;
import net.wowdev.microservices.products.mapper.ProductMapper;
import net.wowdev.microservices.products.service.ProductChangedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

class ProductMessagingTest {
    @Test void publishesProductEvent() {
        final KafkaTemplate<String, Product> template = mock(KafkaTemplate.class);
        final ProductMapper mapper = mock(ProductMapper.class);
        final var source = TestData.product();
        final Product event = Product.newBuilder().setId(TestData.product().getId()).setUnitPrice(java.nio.ByteBuffer.wrap(new byte[]{1}))
                .setName("n").setDescription("d").setCategory("c").build();
        when(mapper.toAvro(source)).thenReturn(event);
        new ProductProducer(template, mapper, "products-topic").publish(new ProductChangedEvent(source));
        verify(template).send("products-topic", source.getId().toString(), event);
    }

    @Test void consumesProduct() {
        new ProductConsumer().consume(null);
    }
}
