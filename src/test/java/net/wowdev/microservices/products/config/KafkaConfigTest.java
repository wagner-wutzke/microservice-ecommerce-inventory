package net.wowdev.microservices.products.config;

import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class KafkaConfigTest {
    @Test void createsConfiguredFactories() throws Exception {
        final KafkaConfig config = new KafkaConfig();
        set(config, "brokers", "localhost:9092");
        set(config, "registry", "http://localhost:8081");
        set(config, "groupId", "products-test");
        final var producerFactory = config.productProducerFactory();
        assertThat(config.productKafkaTemplate(producerFactory)).isNotNull();
        final var consumerFactory = config.productConsumerFactory();
        assertThat(config.productKafkaListenerContainerFactory(consumerFactory, config.productKafkaTemplate(producerFactory))).isNotNull();
    }

    private void set(final Object target, final String name, final String value) throws Exception {
        final Field field = KafkaConfig.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
