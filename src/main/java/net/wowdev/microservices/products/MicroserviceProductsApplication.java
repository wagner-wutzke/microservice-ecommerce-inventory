package net.wowdev.microservices.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MicroserviceProductsApplication {
    public static void main(final String[] args) {
        SpringApplication.run(MicroserviceProductsApplication.class, args);
    }
}
