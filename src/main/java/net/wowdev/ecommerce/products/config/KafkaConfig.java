package net.wowdev.ecommerce.products.config;

import net.wowdev.ecommerce.domain.dto.InventoryDTO;
import net.wowdev.ecommerce.domain.dto.ProductDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String brokers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.acks:all}")
    private String acks;

    @Value("${spring.kafka.producer.properties.delivery.timeout.ms:30000}")
    private String deliveryTimeout;

    @Value("${spring.kafka.producer.properties.linger.ms:0}")
    private String linger;

    @Value("${spring.kafka.producer.properties.request.timeout.ms:10000}")
    private String requestTimeout;

    @Value("${spring.kafka.producer.properties.enable.idempotence:true}")
    private boolean idempotence;

    @Value("${spring.kafka.producer.retries:3}")
    private Integer retries;

    @Value("${spring.kafka.producer.properties.max.in.flight.requests.per.connection}")
    private Integer inflightRequests;

    @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}")
    private String trustedPackages;

    @Bean
    public KafkaTemplate<String, ProductDTO> productKafkaTemplate(final ProducerFactory<String, ProductDTO> factory) {
        return new KafkaTemplate<>(factory);
    }

    @Bean
    public ProducerFactory<String, ProductDTO> productProducerFactory() throws ClassNotFoundException {
        final Map<String, Object> config = new HashMap<>();

        // Mandatory companion of enable.idempotence — Kafka refuses to start the producer
        // Exactly-once-per-partition semantics: the broker deduplicates retried batches
        // using the producer id + sequence number.
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotence);

        // with acks=0 or acks=1 because it cannot deduplicate without full ISR ack.
        config.put(ProducerConfig.RETRIES_CONFIG, retries);

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, acks);
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);
        config.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, inflightRequests);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConsumerFactory<String, ProductDTO> productConsumerFactory() throws ClassNotFoundException {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductDTO> productKafkaListenerContainerFactory(
            final ConsumerFactory<String, ProductDTO> productConsumerFactory,
            final KafkaTemplate<String, ProductDTO> productKafkaTemplate) {
        final var factory = new ConcurrentKafkaListenerContainerFactory<String, ProductDTO>();
        factory.setConsumerFactory(productConsumerFactory);
        factory.setCommonErrorHandler(
                new DefaultErrorHandler(new DeadLetterPublishingRecoverer(productKafkaTemplate),
                new FixedBackOff(2000L, retries)));
        return factory;
    }


    @Bean
    public ProducerFactory<String, InventoryDTO> inventoryProducerFactory() {
        final Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotence);
        config.put(ProducerConfig.RETRIES_CONFIG, retries);
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, acks);
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);
        config.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, inflightRequests);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, InventoryDTO> inventoryKafkaTemplate(
            final ProducerFactory<String, InventoryDTO> factory) {
        return new KafkaTemplate<>(factory);
    }
}
