package example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

    // log4j logger
    private final Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    // Queue receiver
//    @JmsListener(destination = "mailbox", containerFactory = "myQueueFactory")
//    public void receiveQueueMessage(Employee employee) {
//        logger.info("Receiving message from queue: {}", employee);
//        System.out.println("Received from queue <" + employee + ">");
//    }

    // Topic receiver
    @JmsListener(destination = "mytopic", containerFactory = "myTopicFactory", subscription = "S1")
    public void receiveTopicMessage(Employee employee) {
        logger.info("Receiving message from topic: {}", employee);
        System.out.println("Received from topic <" + employee + ">");
    }

}
