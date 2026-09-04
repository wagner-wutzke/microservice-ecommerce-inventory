package net.wowdev.ecommerce.inventory.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.test.util.ReflectionTestUtils;

class ConfigurationTest {
  @Test void configurationClassesCanBeConstructed() {
    assertThat(new DataReplicationConfig()).isNotNull();
    assertThat(new PersistenceConfig()).isNotNull();
  }

  @Test void kafkaFactoriesContainConfiguredProperties() {
    KafkaConfig config = new KafkaConfig();
    ReflectionTestUtils.setField(config, "bootstrapServers", "localhost:9092");
    ReflectionTestUtils.setField(config, "consumerGroup", "inventory");
    ReflectionTestUtils.setField(config, "trustedPackages", "net.wowdev.ecommerce.domain");
    ReflectionTestUtils.setField(config, "acks", "all");
    ReflectionTestUtils.setField(config, "deliveryTimeout", "30000");
    ReflectionTestUtils.setField(config, "linger", "0");
    ReflectionTestUtils.setField(config, "requestTimeout", "10000");
    ReflectionTestUtils.setField(config, "idempotence", true);
    ReflectionTestUtils.setField(config, "retries", 3);
    ReflectionTestUtils.setField(config, "maxRequestsInFlight", 5);
    Object producerFactory = ReflectionTestUtils.invokeMethod(config, "producerFactory");
    Object consumerFactory = ReflectionTestUtils.invokeMethod(config, "consumerFactory");
    assertThat(producerFactory).isNotNull();
    assertThat(consumerFactory).isNotNull();
    assertThat((Object) config.kafkaTemplate(config.producerFactory())).isNotNull();
    ConcurrentKafkaListenerContainerFactory<String, Object> factory = config.kafkaListenerContainerFactory(
        config.consumerFactory(), config.kafkaTemplate(config.producerFactory()));
    assertThat(factory.getContainerProperties().getAckMode().name()).isEqualTo("RECORD");
    assertThat(ReflectionTestUtils.getField(factory, "commonErrorHandler")).isNotNull();
  }
}
