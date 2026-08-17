package net.wowdev.microservices.products.messaging;

import net.wowdev.microservice.ecommerce.dto.ProductDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductConsumer {
    @KafkaListener(topics = "${app.kafka.product-topic}", containerFactory = "productKafkaListenerContainerFactory")
    public void consume(final ProductDTO product) {
        // The consumer is intentionally idempotent: downstream handling can be added without changing the contract.
    }
}
