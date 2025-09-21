package com.example.jms;

import com.example.util.FileLogger;
import io.micronaut.jms.annotations.JMSListener;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.messaging.annotation.MessageBody;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import static io.micronaut.jms.activemq.classic.configuration.ActiveMqClassicConfiguration.CONNECTION_FACTORY_BEAN_NAME;

@Singleton
@JMSListener(CONNECTION_FACTORY_BEAN_NAME)
public class MessageConsumer {
    @Inject
    private FileLogger fileLogger;

    @Queue("msgs.queue")
    public void receive(@MessageBody String message) {
        fileLogger.log("New Customer has been added: "+message);
    }
}
