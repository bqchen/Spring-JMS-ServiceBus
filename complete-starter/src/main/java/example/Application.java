package example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;


@SpringBootApplication
@EnableJms
public class Application {

    // Number of messages to send
    private static int totalSend = 10;
    // log4j logger
    private static final Logger logger = LoggerFactory.getLogger(Application.class);


    public static void main(String[] args) {
        // Launch the application
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

        logger.info("Sending message");

        for (int i = 0; i < totalSend; i++) {
            System.out.printf("Sending message %d.\n", i + 1);
            jmsTemplate.convertAndSend("testqueue", new Email("info@example.com", "Hello"));
        }

    }

}
