package net.wowdev.ecommerce.products.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonConfigTest {
    @Test
    void configuresJavaTimeSerialization() throws Exception {
        final ObjectMapper mapper = new JacksonConfig().objectMapper();

        final String json = mapper.writeValueAsString(Map.of("createdAt", Instant.parse("2026-01-01T00:00:00Z")));

        assertThat(json).contains("2026-01-01T00:00:00Z");
    }
}
