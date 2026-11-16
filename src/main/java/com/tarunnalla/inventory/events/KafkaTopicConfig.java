package com.tarunnalla.inventory.events;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    /** Created on startup if missing. 3 partitions allow up to 3 parallel consumers. */
    @Bean
    public NewTopic stockChangedTopic(@Value("${app.kafka.topics.stock-changed}") String name) {
        return TopicBuilder.name(name)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
