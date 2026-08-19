package net.wowdev.microservices.products.messaging;

import net.wowdev.microservice.ecommerce.dto.ProductDTO;
import net.wowdev.microservices.products.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ProductMessagingTest {
    @Test
    void publishesProductEvent() {
        final KafkaTemplate<String, ProductDTO> template = mock(KafkaTemplate.class);
        final var source = TestData.product();
        new ProductProducer(template, "products-topic").publish(source);
        verify(template).send("products-topic", source.getId().toString(), source);
    }

    @Test
    void consumesProduct() {
        new ProductConsumer().consume(TestData.product());
    }
}
