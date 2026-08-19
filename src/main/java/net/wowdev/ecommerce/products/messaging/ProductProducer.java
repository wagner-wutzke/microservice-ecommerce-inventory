package net.wowdev.ecommerce.products.messaging;

import net.wowdev.ecommerce.domain.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ProductProducer {
    private final KafkaTemplate<String, ProductDTO> kafkaTemplate;
    private final String topic;

    public ProductProducer(final KafkaTemplate<String, ProductDTO> kafkaTemplate,
                           @Value("${app.kafka.product-changes-topic}") final String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(final ProductDTO productDTO) {
        kafkaTemplate.send(topic, productDTO.getId().toString(), productDTO);
    }
}
