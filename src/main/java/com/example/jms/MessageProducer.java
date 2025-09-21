package com.example.jms;

import io.micronaut.jms.annotations.JMSProducer;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;

import static io.micronaut.jms.activemq.classic.configuration.ActiveMqClassicConfiguration.CONNECTION_FACTORY_BEAN_NAME;

@Singleton
@JMSProducer(CONNECTION_FACTORY_BEAN_NAME)
public class MessageProducer {

    private final ConnectionFactory connectionFactory;
    public MessageProducer(ConnectionFactory connectionFactory){
        this.connectionFactory = connectionFactory;
    }
    public void send(String msg) {
        try (JMSContext context = connectionFactory.createContext()) {
            context.createProducer()
                    .send(context.createQueue("msgs.queue"), msg);
        }
    }
}
