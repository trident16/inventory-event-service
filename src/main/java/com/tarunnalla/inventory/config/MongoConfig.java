package com.tarunnalla.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Kept separate from the main application class so that slice tests
 * such as {@code @WebMvcTest} don't try to start MongoDB auditing.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
