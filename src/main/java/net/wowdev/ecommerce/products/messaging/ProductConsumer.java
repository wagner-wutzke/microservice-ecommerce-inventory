package net.wowdev.ecommerce.products.messaging;

import lombok.extern.slf4j.Slf4j;
import net.wowdev.ecommerce.domain.dto.ProductDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductConsumer {
    @KafkaListener(
            topics = "${app.kafka.product-changes-topic}",
            containerFactory = "productKafkaListenerContainerFactory")
    public void consume(final ProductDTO product) {
        // The consumer is intentionally idempotent: downstream handling can be added without changing the contract.
        log.info(">> Consumed product change event {}", product.toString());
    }
}
