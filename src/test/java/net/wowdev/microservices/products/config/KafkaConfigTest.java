package net.wowdev.microservices.products.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;

import net.wowdev.microservice.ecommerce.dto.ProductDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

class KafkaConfigTest {

    @Test
    void createsConfiguredFactories() throws Exception {
        final KafkaConfig config = configuredKafkaConfig();
        final ProducerFactory<String, ProductDTO> producerFactory =
                config.productProducerFactory();
        final KafkaTemplate<String, ProductDTO> kafkaTemplate = config.productKafkaTemplate(producerFactory);
        final ConsumerFactory<String, ProductDTO> consumerFactory = config.productConsumerFactory();
        final ConcurrentKafkaListenerContainerFactory<String, ProductDTO> listenerFactory =
                config.productKafkaListenerContainerFactory(consumerFactory, kafkaTemplate);

        final Map<String, Object> producerProperties =
                ((DefaultKafkaProducerFactory<String, ProductDTO>) producerFactory).getConfigurationProperties();
        final Map<String, Object> consumerProperties =
                ((DefaultKafkaConsumerFactory<String, ProductDTO>) consumerFactory).getConfigurationProperties();

        assertThat(kafkaTemplate).isNotNull();
        assertThat(producerProperties)
                .containsEntry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
                .containsEntry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
                .containsEntry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonSerializer")
                .containsEntry(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
                .containsEntry(ProducerConfig.RETRIES_CONFIG, 3)
                .containsEntry(ProducerConfig.ACKS_CONFIG, "all")
                .containsEntry(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        assertThat(consumerProperties)
                .containsEntry(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
                .containsEntry(ConsumerConfig.GROUP_ID_CONFIG, "products-test");
        assertThat(listenerFactory).isNotNull();
        assertThat(listenerFactory.getConsumerFactory()).isSameAs(consumerFactory);
    }

    private KafkaConfig configuredKafkaConfig() throws Exception {
        final KafkaConfig config = new KafkaConfig();
        set(config, "brokers", "localhost:9092");
        set(config, "bootstrapServers", "localhost:9092");
        set(config, "registry", "http://localhost:8081");
        set(config, "groupId", "products-test");
        set(config, "keySerializer", "org.apache.kafka.common.serialization.StringSerializer");
        set(config, "valueSerializer", "io.confluent.kafka.serializers.KafkaJsonSerializer");
        set(config, "acks", "all");
        set(config, "deliveryTimeout", "30000");
        set(config, "linger", "0");
        set(config, "requestTimeout", "10000");
        set(config, "idempotence", true);
        set(config, "retries", 3);
        set(config, "inflightRequests", 5);
        return config;
    }

    private void set(final Object target, final String name, final Object value) throws Exception {
        final Field field = KafkaConfig.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
