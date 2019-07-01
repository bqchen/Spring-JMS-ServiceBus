package example.config;

import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;

@Component
public class MessagingConfig {

    @Value("${azure.servicebus.connection-string}")
    private String connectionString;

    @Value("${spring.application.name}")
    private String clientId;

    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        ConnectionStringBuilder csb = new ConnectionStringBuilder(connectionString);
        String remoteUri = String.format("amqps://%s?amqp.idleTimeout=3600000", csb.getEndpoint().getHost());
        JmsConnectionFactory jmsConnectionFactory = new JmsConnectionFactory(remoteUri);
        jmsConnectionFactory.setRemoteURI(remoteUri);
        jmsConnectionFactory.setClientID(clientId);
        jmsConnectionFactory.setUsername(csb.getSasKeyName());
        jmsConnectionFactory.setPassword(csb.getSasKey());
//        jmsConnectionFactory.setReceiveLocalOnly(true);
        return new CachingConnectionFactory(jmsConnectionFactory);
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory jmsConnectionFactory) {
        JmsTemplate returnValue = new JmsTemplate();
        returnValue.setConnectionFactory(jmsConnectionFactory);
        return returnValue;
    }

    // JmsListenerContainerFactory for Queue
    @Bean
    public JmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory returnValue = new DefaultJmsListenerContainerFactory();
        returnValue.setConnectionFactory(connectionFactory);
        return returnValue;
    }

//    // JmsListenerContainerFactory for Topic
//    @Bean
//    public JmsListenerContainerFactory topicJmsListenerContainerFactory(ConnectionFactory connectionFactory) {
//        DefaultJmsListenerContainerFactory returnValue = new DefaultJmsListenerContainerFactory();
//        returnValue.setConnectionFactory(connectionFactory);
//        returnValue.setSubscriptionDurable(Boolean.TRUE);
//        return returnValue;
//    }

//    @Bean // Serialize message content to json using TextMessage
//    public MessageConverter jacksonJmsMessageConverter() {
//        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//        converter.setTargetType(MessageType.TEXT);
//        converter.setTypeIdPropertyName("_type");
//        return converter;
//    }

}
