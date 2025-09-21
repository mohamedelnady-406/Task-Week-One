package com.example.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@KafkaListener
public class CustomerEventConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(String.class);

    @Topic("customer-events")
    public void receive(String str){
        LOG.info("Consumed customer from Kafka:"+str);

    }

}
