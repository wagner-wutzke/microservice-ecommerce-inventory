package net.wowdev.ecommerce.inventory.messaging;

import static org.mockito.Mockito.*;

import java.util.UUID;
import net.wowdev.ecommerce.domain.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

class ProductProducerTest {
  @Test void publishesProductUsingProductIdAsKey() {
    KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);
    ProductProducer producer = new ProductProducer(kafka);
    ReflectionTestUtils.setField(producer, "topic", "products");
    ProductDTO product = new ProductDTO();
    UUID id = UUID.randomUUID();
    product.setId(id);
    producer.publish(product);
    verify(kafka).send("products", id.toString(), product);
  }
}
