package net.wowdev.microservices.products.messaging;

import net.wowdev.microservices.products.avro.Product;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductConsumer {
    @KafkaListener(topics = "${app.kafka.product-topic}", containerFactory = "productKafkaListenerContainerFactory")
    public void consume(final Product product) {
        // The consumer is intentionally idempotent: downstream handling can be added without changing the contract.
    }
}
