package example;

import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;


@SpringBootApplication
@EnableJms
public class Application {

    @Value("${spring.jms.servicebus.connection-string}")
    private String connectionString;

    @Value("${spring.jms.servicebus.topic-client-id}")
    private String clientId;

    @Bean
    public ConnectionFactory myConnectionFactory() {
        ConnectionStringBuilder connectionStringBuilder = new ConnectionStringBuilder(connectionString);
        String remoteUri = "amqps://" + connectionStringBuilder.getEndpoint().getHost();
        JmsConnectionFactory connectionFactory = new JmsConnectionFactory(remoteUri);
        connectionFactory.setRemoteURI(remoteUri);
        connectionFactory.setClientID(clientId);
        connectionFactory.setUsername(connectionStringBuilder.getSasKeyName());
        connectionFactory.setPassword(connectionStringBuilder.getSasKey());
        return new CachingConnectionFactory(connectionFactory);
    }

//    @Bean
//    public JmsListenerContainerFactory<?> myQueueFactory(ConnectionFactory connectionFactory,
//                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
//        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//        // This provides all boot's default to this factory, including the message converter
//        configurer.configure(factory, connectionFactory);
//        // You could still override some of Boot's default if necessary.
//        return factory;
//    }

    @Bean
    public JmsListenerContainerFactory<?> myTopicFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory topicFactory = new DefaultJmsListenerContainerFactory();
        topicFactory.setConnectionFactory(connectionFactory);
        topicFactory.setSubscriptionDurable(Boolean.TRUE);
        return topicFactory;
    }

    public static void main(String[] args) {
        // Launch the application
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

        // Send a message with a POJO
        System.out.println("Sending a welcome message.");
//        jmsTemplate.convertAndSend("mailbox", new Employee("exampleName", "10001"));
        jmsTemplate.convertAndSend("mytopic", new Employee("exampleName", "10001"));
    }


}
