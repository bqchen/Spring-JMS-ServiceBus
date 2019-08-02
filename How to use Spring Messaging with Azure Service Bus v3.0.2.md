---
title: Spring Messaging with Azure Service Bus
description: This article demonstrates how to use Spring Java Message Bus (JMS) to send messages to and receive messages from Azure Service Bus.
author: Sean Li
manager: 
ms.author: 
ms.date: 07/30/2019
ms.devlang: java
ms.service: azure-java
ms.topic: article
---

# How to use Spring Messaging with Azure Service Bus

## Overview

Azure provides an asynchronous messaging platform called [Azure Service Bus](https://docs.microsoft.com/azure/service-bus-messaging/service-bus-messaging-overview) that is based on the Advanced Message Queueing Protocol 1.0 ([AMQP 1.0](http://www.amqp.org/)) standard. Service Bus can be used across the range of supported Azure platforms.

This article demonstrates how to use Spring Java Message Service (JMS) to send messages and receive messages from Azure Service Bus **Queues** and **Topics** in your own application.

## Prerequisites

The following prerequisites are required for this article:

1. An Azure subscription; if you don't already have an Azure subscription, you can activate your [MSDN subscriber benefits](https://azure.microsoft.com/pricing/member-offers/credit-for-visual-studio-subscribers/) or sign up for a [free account](https://azure.microsoft.comfree/).

2. A supported Java Development Kit (JDK), version 8 or later. For more information about the JDKs available for use when developing on Azure, see <https://aka.ms/azure-jdks>.

3. Apache's [Maven](http://maven.apache.org/), version 3.2 or later.

4. If you don't have a queue to work with, follow steps in the [Use Azure portal to create a Service Bus queue](https://docs.microsoft.com/azure/service-bus-messaging/service-bus-quickstart-portal) to create a queue.

    a. Create a Service Bus **namespace**.

    b. Get the **connection string**.

    c. Create a Service Bus **queue**.

5. If you don't have a topic and a subscription to work with, follow steps in the [Use the Azure portal to create a Service Bus topic and subscriptions to the topic](https://docs.microsoft.com/azure/service-bus-messaging/service-bus-quickstart-topics-subscriptions-portal) to create a topic and a subscription.

    a. Create a Service Bus **namespace**.

    b. Get the **connection string**.

    c. Create a Service Bus **topic**.

    d. Create a **subscription** to the topic in the namespace.

6. If you don't have a Spring Boot application, you can create your **Maven** project with the [Spring Initializr](https://start.spring.io/).

> [!NOTE]
The Service Bus namespace must allow access from all networks, be Premium or higher (lower tiers have some limitations), and have an access policy with read/write access for your queue / topic.

> [!IMPORTANT]
> Spring Boot version 2.0 or greater is required to complete the steps in this article.

> [!TIP]
> The project we build here is available on <https://github.com/Azure-Samples/spring-jms-service-bus>, so you can use that sample repository directly if you want to see the final work that is detailed in this tutorial.

## Create sample modules in your Spring Boot application

1. Locate the *pom.xml* file in the directory of your app; for example:

    `C:\spring-jms-service-bus\spring-jms-servicebus-sample\pom.xml`

    -or-

    `/users/example/home/spring-jms-servicebus-sample/pom.xml`

2. Verify that the Spring Boot version is the version that you chose when you created your application; for example:

    ```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.6.RELEASE</version>
    </parent>
    ```

3. Create `azure-servicebus-queue-spring-jms-sample` module in your Spring Boot application to implement Spring messaging with Service Bus queue.

4. Create `azure-servicebus-topic-spring-jms-sample` module in your Spring Boot application to implement Spring messaging with Service Bus topic.

## Configure your Spring Boot application to use Spring JMS

1. Locate the *pom.xml* file in the directory of `azure-servicebus-queue-spring-jms-sample` module or `azure-servicebus-topic-spring-jms-sample` module; for example:

    `C:\spring-jms-service-bus\spring-jms-servicebus-sample\azure-servicebus-queue-spring-jms-sample\pom.xml`

    -or-

    `/users/example/home/spring-jms-servicebus-sample/azure-servicebus-queue-spring-jms-sample/pom.xml`

2. Open the *pom.xml* file in a text editor and add the following lines to list of `<dependencies>`:

    ```xml
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-jms</artifactId>
        <version>5.1.8.RELEASE</version>
    </dependency>
    ```

    ![project-new1](https://github.com/bqchen/Spring-JMS-ServiceBus/raw/tutorial/figure/project-pom-new1.jpg)

3. Save and close the *pom.xml* file.

## Configure your Spring Boot application to use Qpid JMS Client

In a communication system that uses message middleware, there are two kinds of roles: broker and client. Because Service Bus is a fully managed enterprise integration message broker and it supports AMQP 1.0 for JMS, you need clients supporting AMQP 1.0 as well. As a third party open source component managed by the [Apache Qpid Project](https://qpid.apache.org/), the [Qpid JMS](http://qpid.apache.org/components/jms/index.html) client is compatible with Service Bus via its AMQP 1.0 protocol support. Therefore, in this article, the Qpid JMS client is chosen and retrieved from Maven. For information about the latest version of the Apache Qpid JMS AMQP 1.0 client library, see [Qpid JMS - AMQP 1.0](https://qpid.apache.org/maven.html#qpid-jms-amqp-10).

1. Locate the *pom.xml* file in the directory of `azure-servicebus-queue-spring-jms-sample` module or `azure-servicebus-topic-spring-jms-sample` module; for example:

    `C:\spring-jms-service-bus\spring-jms-servicebus-sample\azure-servicebus-queue-spring-jms-sample\pom.xml`

    -or-

    `/users/example/home/spring-jms-servicebus-sample/azure-servicebus-queue-spring-jms-sample/pom.xml`

2. Open the *pom.xml* file in a text editor and add the following lines to list of `<dependencies>`:

    ```xml
    <dependency>
        <groupId>org.apache.qpid</groupId>
        <artifactId>qpid-jms-client</artifactId>
        <version>0.43.0</version>
    </dependency>
    ```

    ![project-pom-new2](https://github.com/bqchen/Spring-JMS-ServiceBus/raw/tutorial/figure/project-pom-new2.jpg)

3. Save and close the *pom.xml* file.

## Configure your Spring Boot app to use your Service Bus

1. Locate the *application.properties* in the *resources* directory of `azure-servicebus-queue-spring-jms-sample` module or `azure-servicebus-topic-spring-jms-sample` module; for example:

    `C:\spring-jms-service-bus\spring-jms-servicebus-sample\azure-servicebus-queue-spring-jms-sample\application.properties`

    -or-

    `/users/example/home/spring-jms-servicebus-sample/azure-servicebus-queue-spring-jms-sample/application.properties`

2. Open the *application.properties* file in `azure-servicebus-queue-spring-jms-sample` module in a text editor, and add the following lines when you use Service Bus queue.

    > [!NOTE]
    > Replace `<ServiceBusNamespaceConnectionString>` with the connection string of your Service Bus namespace.
    >
    > Replace `<IdleTimeout>` with the idle timeout you prefer; 1800000 is recommended.

    ```yml
    spring.jms.servicebus.connection-string=<ServiceBusNamespaceConnectionString>
    spring.jms.servicebus.idle-timeout=<IdleTimeout>
    ```

    Open the *application.properties* file in `azure-servicebus-topic-spring-jms-sample` module in a text editor, and add the following lines when you use Service Bus topic.

    > [!NOTE]
    > Replace `<ServiceBusNamespaceConnectionString>` with the connection string of your Service Bus namespace.
    >
    > Replace `<ServiceBusTopicClientId>` with the client id for your Service Bus topic.
    >
    > Replace `<IdleTimeout>` with the idle timeout you prefer; 1800000 is recommended.

    ```yml
    spring.jms.servicebus.connection-string=<ServiceBusNamespaceConnectionString>
    spring.jms.servicebus.topic-client-id=<ServiceBusTopicClientId>
    spring.jms.servicebus.idle-timeout=<IdleTimeout>
    ```

    Where:
    | Field | Description|
    | :---------------------------------------- | :----------- |
    | `spring.jms.servicebus.connection-string` | Specifies the Service Bus connection string in your namespace created earlier in this tutorial |
    | `spring.jms.servicebus.topic-client-id`   | Specifies the JMS client id when using Service Bus topic and durable subscription        |
    |`spring.jms.servicebus.idle-timeout`       | Specifies the idle timeout in milliseconds|

3. Save and close the *application.properties* file.

## Add sample code to implement basic Service Bus functionality

In this section, you create the necessary Java classes for sending messages to your Service Bus queue or topic and receive messages from your corresponding queue or subscription.

### Define a simple Java class

1. Locate into the directory of `azure-servicebus-queue-spring-jms-sample` module or `azure-servicebus-topic-spring-jms-sample` module.

2. Create a new Java file named *User.java* in the package directory of your corresponding module.

3. Open the *User.java* file in a text editor, and add the following lines to define a generic user class that stores and retrieves user's name:

    ```java
    package sample;

    import java.io.Serializable;

    // Define a generic User class.
    public class User implements Serializable {

        private static final long serialVersionUID = -295422703255886286L;

        private String name;

        public User() {
        }

        public User(String name) {
            setName(name);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
    ```

    Here, `Serializable` is implemented in order to use the send method in `JmsTemplate` in Spring framework correctly. Otherwise, a customized `MessageConverter` bean should be defined to serialize the content to json in text format (i.e. as a `TextMessage`). For more details about `MessageConverter`, see the official [Spring JMS starter project](https://spring.io/guides/gs/messaging-jms/).

4. Save and close the *User.java* file.

### Create a new class for storing Service Bus key

1. Create a new Java file named *ServiceBusKey.java* in the package directory of your corresponding module, then open the file in a text editor and add the following lines:

    ```java
    package sample;

    public class ServiceBusKey {
        private final String host;
        private final String sharedAccessKeyName;
        private final String sharedAccessKey;

        ServiceBusKey(String host, String sharedAccessKeyName, String sharedAccessKey) {
            this.host = host;
            this.sharedAccessKeyName = sharedAccessKeyName;
            this.sharedAccessKey = sharedAccessKey;
        }

        public String getHost() {
            return host;
        }

        public String getSharedAccessKeyName() {
            return sharedAccessKeyName;
        }

        public String getSharedAccessKey() {
            return sharedAccessKey;
        }
    }
    ```

2. Save and close the *ServiceBusKey.java* file.

### Create a new class for parsing the connection string

1. Create a new Java file named *ConnectionStringResolver.java* in the package directory of your corresponding module, then open the file in a text editor and add the following lines:

    ```java
    package sample;

    import java.util.HashMap;

    public class ConnectionStringResolver {

        private static final String ENDPOINT = "Endpoint";
        private static final String HOST = "host";
        private static final String SAS_KEY_NAME = "SharedAccessKeyName";
        private static final String SAS_KEY = "SharedAccessKey";

        public static ServiceBusKey getServiceBusKey(String connectionString) {
            final String[] segments = connectionString.split(";");
            final HashMap<String, String> hashMap = new HashMap<>();

            for (final String segment : segments) {
                final int indexOfEqualSign = segment.indexOf("=");
                final String key = segment.substring(0, indexOfEqualSign);
                final String value = segment.substring(indexOfEqualSign + 1);
                hashMap.put(key, value);
            }

            final String endpoint = hashMap.get(ENDPOINT);
            final String[] segmentsOfEndpoint = endpoint.split("/");
            final String host = segmentsOfEndpoint[segmentsOfEndpoint.length - 1];
            hashMap.put(HOST, host);

            return new ServiceBusKey(hashMap.get(HOST), hashMap.get(SAS_KEY_NAME), hashMap.get(SAS_KEY));
        }

    }
    ```

2. Save and close the *ConnectionStringResolver.java* file.

### Create a new class for the message send controller

1. If you want to send messages to your Service Bus queue, create a new Java file named *QueueSendController.java* in the package directory of your corresponding module, then open the file in a text editor and add the following lines:

    ```java
    package sample;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.jms.core.JmsTemplate;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    public class QueueSendController {

        private static final String QUEUE_NAME = "<ServiceBusQueueName>";

        private static final Logger logger = LoggerFactory.getLogger(QueueSendController.class);

        @Autowired
        private JmsTemplate jmsTemplate;

        @PostMapping("/queue")
        public String postMessage(@RequestParam String message) {
            logger.info("Sending message");
            jmsTemplate.convertAndSend(QUEUE_NAME, new User(message));
            return message;
        }
    }
    ```

    If you want to send messages to your Service Bus topic, create a new Java file named *TopicSendController.java* in the package directory of your corresponding module. Copy the above code into your file and make the following changes:

    - Change `QueueSendController` into `TopicSendController`.
    - Change `QUEUE_NAME` into `TOPIC_NAME`.
    - Change `<ServiceBusQueueName>` into `<ServiceBusTopicName>`.

    > [!NOTE]
    > Replace `<ServiceBusQueueName>` with your own quene name configured in your Service Bus namespace when you use Service Bus queue.
    >
    >Replace `<ServiceBusTopicName>` with your own topic name configured in your Service Bus namespace when you use Service Bus topic.

1. Save and close the message send controller Java file.

### Define customized Connection Factory

Although `ConnectionFactory` can be created automatically by Spring Boot, when integrating Spring messaging with Azure Service Bus, a customized `ConnectionFactory` should be defined to configure the properties of your Service Bus namespace.

1. Locate the main application Java file in the package directory of your corresponding module; for example:

    `C:\spring-jms-service-bus\spring-jms-servicebus-sample\azure-servicebus-queue-spring-jms-sample\src\main\java\sample\ServiceBusJMSQueueApplication.java`

    -or-

    `/users/example/home/spring-jms-servicebus-sample/azure-servicebus-queue-spring-jms-sample/src/main/java/sample/ServiceBusJMSQueueApplication.java`

2. If you want to send messages to your Service Bus queue, open the main application Java file in a text editor, and add the following lines to the file before `main()` method to define and configure your own `ConnectionFactory`:

    ```java
    @Value("${spring.jms.servicebus.connection-string}")
    private String connectionString;

    @Value("${spring.jms.servicebus.idle-timeout}")
    private int idleTimeout;

    private static final String AMQP_URI_FORMAT = "amqps://%s?amqp.idleTimeout=%d";

    @Bean
    public ConnectionFactory myConnectionFactory() {
        ServiceBusKey serviceBusKey = ConnectionStringResolver.getServiceBusKey(connectionString);
        String host = serviceBusKey.getHost();
        String sasKeyName = serviceBusKey.getSharedAccessKeyName();
        String sasKey = serviceBusKey.getSharedAccessKey();

        String remoteUri = String.format(AMQP_URI_FORMAT, host, idleTimeout);
        JmsConnectionFactory jmsConnectionFactory = new JmsConnectionFactory();
        jmsConnectionFactory.setRemoteURI(remoteUri);
        jmsConnectionFactory.setUsername(sasKeyName);
        jmsConnectionFactory.setPassword(sasKey);
        return new CachingConnectionFactory(jmsConnectionFactory);
    }

    ```

    If you want to send messages to your Service Bus topic, open the corresponding main application Java file in a text editor, copy the above code to the file before `main()` method and make the following changes:

    - Add the following code before the definition of `AMQP_URI_FORMAT`:

        ```java
        @Value("${spring.jms.servicebus.topic-client-id}")
        private String clientId = "";
        ```

    - Add the following code in `myConnectionFactory` bean:

        ```java
        jmsConnectionFactory.setClientID(clientId);
        ```

3. Save and close the main application Java file.

### Create a new class for the message receive controller

#### Receive messages from a Service Bus queue

1. Locate the main application Java file in the package directory of your corresponding module; for example:

    `C:\spring-jms-service-bus\spring-jms-servicebus-sample\azure-servicebus-queue-spring-jms-sample\src\main\java\sample\ServiceBusJMSQueueApplication.java`

    -or-

    `/users/example/home/spring-jms-servicebus-sample/azure-servicebus-queue-spring-jms-sample/src/main/java/sample/ServiceBusJMSQueueApplication.java`

2. Define a `myQueueFactory` bean in order to receive messages from a Service Bus queue. Open the main application Java file in a text editor, and add the following lines to the file before `main()` method:

    ```java
    @Bean
    public JmsListenerContainerFactory<?> myQueueFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory queueFactory = new DefaultJmsListenerContainerFactory();
        queueFactory.setConnectionFactory(connectionFactory);
        return queueFactory;
    }
    ```

3. Save and close the main application Java file.

4. Create a new Java file named *QueueReceiveController.java* in the package directory of your corresponding module, then open the file in a text editor and add the following lines:

    ```java
    package sample;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.jms.annotation.JmsListener;
    import org.springframework.stereotype.Component;

    @Component
    public class QueueReceiveController {

        private static final String QUEUE_NAME = "<ServiceBusQueueName>";

        private final Logger logger = LoggerFactory.getLogger(QueueReceiveController.class);

        @JmsListener(destination = QUEUE_NAME, containerFactory = "myQueueFactory")
        public void receiveQueueMessage(User user) {
            logger.info("Receiving message from queue: {}", user.getName());
        }

    }
    ```

    The **`JMSListener`** annotation defines the following:

   - **`Destination`** - The name of the queue that the method should listen to.
   - **`JmsListenerContainerFactory`** - The reference used to create the underlying message listener container. The **`containerFactory`** value must be assigned.
   - **`receiveQueueMessage()`** - A method that receives messages from the queue and prints logs.
  
    > [!NOTE]
Replace `<ServiceBusQueueName>` with your own queue name configured in your Service Bus namespace.
  
5. Save and close the *QueueReceiveController.java* file.

#### Receive messages from a Service Bus subscription

1. Locate the main application Java file in the package directory of your corresponding module; for example:

    `C:\spring-jms-service-bus\spring-jms-servicebus-sample\azure-servicebus-queue-spring-jms-sample\src\main\java\sample\ServiceBusJMSTopicApplication.java`

    -or-

    `/users/example/home/spring-jms-servicebus-sample/azure-servicebus-queue-spring-jms-sample/src/main/java/sample/ServiceBusJMSTopicApplication.java`

2. Define a `myTopicFactory` bean in order to receive messages from a Service Bus subscription. Open the main application Java file in a text editor, and add the following lines to the file before `main()` method. Remember to create a durable subscription.

    ```java
    @Bean
    public JmsListenerContainerFactory<?> myTopicFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory topicFactory = new DefaultJmsListenerContainerFactory();
        topicFactory.setConnectionFactory(connectionFactory);
        topicFactory.setSubscriptionDurable(Boolean.TRUE);
        return topicFactory;
    }
    ```

3. Save and close the main application Java file.

4. Create a new Java file named *TopicReceiveController.java* in the package directory of your corresponding module, then open the file in a text editor and add the following lines:

    ```java
    package sample;

    import org.apache.qpid.jms.JmsConnectionFactory;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.context.annotation.Bean;
    import org.springframework.jms.annotation.JmsListener;
    import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
    import org.springframework.jms.config.JmsListenerContainerFactory;
    import org.springframework.jms.connection.CachingConnectionFactory;
    import org.springframework.stereotype.Component;

    import javax.jms.ConnectionFactory;

    @Component
    public class TopicReceiveController {

        private static final String TOPIC_NAME = "<ServiceBusTopicName>";

        private static final String SUBSCRIPTION_NAME = "<ServiceBusSubscriptionName>";

        private final Logger logger = LoggerFactory.getLogger(TopicReceiveController.class);

        @JmsListener(destination = TOPIC_NAME, containerFactory = "myTopicFactory", subscription = SUBSCRIPTION_NAME)
        public void receiveTopicMessage(User user) {
            logger.info("Received message from topic: {}", user.getName());
        }

    }
    ```

    The **`JMSListener`** annotation defines the following:

   - **`Destination`** - The name of the topic that the method should listen to.
   - **`Subscription`** - The service bus subscription name.
   - **`JmsListenerContainerFactory`** - The reference used to create the underlying message listener container. The **`containerFactory`** value must be assigned.
   - **`receiveTopicMessage()`** - A method that receives messages from the subscription and prints logs.

    > [!NOTE]
    > Replace `<ServiceBusTopicName>` with your own topic name configured in your Service Bus namespace. Also replace `<ServiceBusSubscriptionName>` with your own subscription name for your Service Bus topic.

5. Save and close the *TopicReceiveController.java* file.

### Update the main application class

1. Locate the main application Java file in the package directory of your corresponding module; for example:

    `C:\spring-jms-service-bus\spring-jms-servicebus-sample\azure-servicebus-queue-spring-jms-sample\src\main\java\sample\ServiceBusJMSQueueApplication.java`

    -or-

    `/users/example/home/spring-jms-servicebus-sample/azure-servicebus-queue-spring-jms-sample/src/main/java/sample/ServiceBusJMSQueueApplication.java`

2. Open the main application Java file in a text editor, and add the following imports and annotations, and update the `main()` method:

    ```java
    package sample;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.jms.annotation.EnableJms;
    import org.apache.qpid.jms.JmsConnectionFactory;
    import org.springframework.context.annotation.Bean;
    import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
    import org.springframework.jms.config.JmsListenerContainerFactory;
    import org.springframework.jms.connection.CachingConnectionFactory;
    import org.springframework.beans.factory.annotation.Value;
    import javax.jms.ConnectionFactory;

    @SpringBootApplication
    @EnableJms
    public class ServiceBusJMSQueueApplication {

        public static void main(String[] args) {
            SpringApplication.run(ServiceBusJMSQueueApplication.class, args);
        }

    }
    ```

    If you want to use Service Bus topic, configure the corresponding main application Java file in the same way as described above.

3. Save and close the main application Java file.

## Build and test your application

### Build and test your application with Service Bus queue

1. Open a command prompt and change directory to the folder of `azure-servicebus-queue-spring-jms-sample` module; for example:

    `cd C:\spring-jms-service-bus\spring-jms-servicebus-sample\azure-servicebus-queue-spring-jms-sample`

    -or-

    `cd /users/example/home/spring-jms-servicebus-sample/azure-servicebus-queue-spring-jms-sample`

2. Build your Spring Boot application with Maven and run it; for example:

    ```bash
    mvn clean spring-boot:run
    ```

3. Once your application is running, you can use *curl* to test your application; for example:

    ```bash
    curl -X POST localhost:8080/queue?message=hello
    ```

    You will see "Sending message" and "hello" posted to your application log. For example:

    ```bash
    [nio-8080-exec-1] sample.QueueSendController : Sending message
    [enerContainer-1] sample.QueueReceiveController : Receiving message from queue: hello
    ```

### Build and test your application with Service Bus topic

1. Open a command prompt and change directory to the folder of `azure-servicebus-topic-spring-jms-sample` module; for example:

    `cd C:\spring-jms-service-bus\spring-jms-servicebus-sample\azure-servicebus-topic-spring-jms-sample`

    -or-

    `cd /users/example/home/spring-jms-servicebus-sample/azure-servicebus-topic-spring-jms-sample`

2. Build your Spring Boot application with Maven and run it; for example:

    ```bash
    mvn clean spring-boot:run
    ```

3. Once your application is running, you can use *curl* to test your application; for example:

    ```bash
    curl -X POST localhost:8080/topic?message=hello
    ```

    You will see "Sending message" and "hello" posted to your application log. For example:

    ```bash
    [nio-8080-exec-1] sample.TopicSendController : Sending message
    [enerContainer-1] sample.TopicReceiveController : Receiving message from topic: hello
    ```

## Next steps

For more information about using Azure Service Bus with JMS, see [How to use JMS API with Service Bus and AMQP 1.0](https://docs.microsoft.com/azure/service-bus-messaging/service-bus-java-how-to-use-jms-api-amqp).

For more information about using Java with the Azure platform see [Azure for Java cloud developers](https://docs.microsoft.com/en-us/azure/java)

For more information on using the Spring framework in your app see the Spring web site [Spring](https://spring.io/). It provides developers with a JMS integration framework, simplifying the use of the JMS API.
