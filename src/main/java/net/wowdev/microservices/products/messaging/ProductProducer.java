package net.wowdev.microservices.products.messaging;

import net.wowdev.microservices.products.avro.Product;
import net.wowdev.microservices.products.mapper.ProductMapper;
import net.wowdev.microservices.products.service.ProductChangedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ProductProducer {
    private final KafkaTemplate<String, Product> kafkaTemplate;
    private final ProductMapper mapper;
    private final String topic;

    public ProductProducer(final KafkaTemplate<String, Product> kafkaTemplate, final ProductMapper mapper,
                           @Value("${app.kafka.product-topic}") final String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(final ProductChangedEvent event) {
        final net.wowdev.microservices.products.domain.Product product = event.product();
        kafkaTemplate.send(topic, product.getId().toString(), mapper.toAvro(product));
    }
}
