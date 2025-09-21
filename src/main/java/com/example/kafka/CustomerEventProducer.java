package com.example.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface CustomerEventProducer {
    @Topic("customer-events")
    void sendCustomerCreated(String str);
}
