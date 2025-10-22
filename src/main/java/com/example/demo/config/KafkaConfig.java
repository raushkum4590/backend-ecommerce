package com.example.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaConfig {

    public static final String ORDER_NOTIFICATION_TOPIC = "order-notifications";
    public static final String ORDER_STATUS_TOPIC = "order-status-updates";
    public static final String PAYMENT_NOTIFICATION_TOPIC = "payment-notifications";

    @Bean
    public NewTopic orderNotificationTopic() {
        return TopicBuilder.name(ORDER_NOTIFICATION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderStatusTopic() {
        return TopicBuilder.name(ORDER_STATUS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentNotificationTopic() {
        return TopicBuilder.name(PAYMENT_NOTIFICATION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

