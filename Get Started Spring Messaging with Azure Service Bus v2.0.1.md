# Get Started: Spring Messaging with Azure Service Bus

## Overview

Azure provides ever-expanding cloud computing services. With large-scale distributed application systems emerging, it is of great importance to ensure secure, reliable and efficient message delivering. To achieve these goals, Azure provides an asynchronous messaging platform called [Azure Service Bus](https://docs.microsoft.com/azure/service-bus-messaging/service-bus-messaging-overview). Because it is based on the Advanced Message Queueing Protocol 1.0 ([AMQP 1.0](http://www.amqp.org/)) standard, Service Bus can be used across the range of supported Azure platforms.

As one of the most popular languages in the world, Java is supported in Azure. We have several [documentations](https://docs.microsoft.com/azure/java/?view=azure-java-stable
) for Java developers. When using Azure Service Bus in your Java applications, you can refer to the [documentation](https://docs.microsoft.com/azure/service-bus-messaging/service-bus-java-how-to-use-jms-api-amqp) to learn how to use Service Bus resources via the Java Message Service ([JMS](https://www.oracle.com/technetwork/java/jms/index.html)) specification. Nowadays, as an open-source application framework developed by [Pivotal](https://pivotal.io/), [Spring](https://spring.io/) is popular among more and more Java developers. It provides developers with a JMS integration framework, simplifying the use of the JMS API.

This article demonstrates how to use Spring JMS to send messages to and receive messages from Azure Service Bus **Queues** and **Topics** in your own application.

<span id="Prerequisites"></span>
## Prerequisites


The following prerequisites are required in order to follow the steps in this article:

1. An Azure subscription; if you don't already have an Azure subscription, you can activate your [MSDN subscriber benefits](https://azure.microsoft.com/pricing/member-offers/credit-for-visual-studio-subscribers/) or sign up for a [free account](https://azure.microsoft.comfree/).

2. A supported Java Development Kit (JDK). For more information about the JDKs available for use when developing on Azure, see <https://aka.ms/azure-jdks>.

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

> **Note**
> >
> The Service Bus namespace must:
> >
> &emsp;*a. allow access from all networks*
> >
> &emsp;*b. be Premium or higher (lower tiers have some limitations)*
> >
> &emsp;*c. have an access policy with read/write access for your queue / topic*

## Configure your Spring Boot application to use the Azure Service Bus starter

1. After creating your Spring Boot application, locate the `pom.xml` file in the directory of your app; for example:

    `C:\Spring-JMS-ServiceBus\starter\pom.xml`

    -or-

    `/users/example/home/starter/pom.xml`

2. Open the `pom.xml` file in a text editor and add the following lines to list of `<dependencies>`:

    ```xml
    <dependency>
        <groupId>com.microsoft.azure</groupId>
        <artifactId>azure-servicebus</artifactId>
        <version>1.2.12</version>
    </dependency>
    ```

    ![project-pom1](https://github.com/bqchen/Spring-JMS-ServiceBus/raw/master/figure/project-pom1.jpg)

3. Verify that the Spring Boot version is the version that you chose when you created your application; for example:

    ```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.6.RELEASE</version>
    </parent>
    ```

## Configure your Spring Boot application to use Spring JMS

1. Locate the `pom.xml` file in the directory of your app; for example:

    `C:\Spring-JMS-ServiceBus\starter\pom.xml`

    -or-

    `/users/example/home/starter/pom.xml`

2. Open the `pom.xml` file in a text editor and add the following lines to list of `<dependencies>`:

    ```xml
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-jms</artifactId>
        <version>5.1.8.RELEASE</version>
    </dependency>
    ```

    ![project-pom2](https://github.com/bqchen/Spring-JMS-ServiceBus/raw/master/figure/project-pom2.jpg)

## Configure your Spring Boot application to use Qpid JMS Client

In a communication system that uses message middleware, there are two kinds of roles: broker and client. Because Service Bus is a fully managed enterprise integration message broker and it supports AMQP 1.0 for JMS, you need clients supporting AMQP 1.0 as well. As a third party open source component managed by the [Apache Qpid Project](https://qpid.apache.org/), the [Qpid JMS](http://qpid.apache.org/components/jms/index.html) client is compatible with Service Bus via its AMQP 1.0 protocol support. Therefore, in this article, the Qpid JMS client is chosen and retrieved from Maven. For information about the latest version of the Apache Qpid JMS AMQP 1.0 client library, see [Qpid JMS - AMQP 1.0](https://qpid.apache.org/maven.html#qpid-jms-amqp-10).

1. Locate the `pom.xml` file in the directory of your app; for example:

    `C:\Spring-JMS-ServiceBus\starter\pom.xml`

    -or-

    `/users/example/home/starter/pom.xml`

2. Open the `pom.xml` file in a text editor and add the following lines to list of `<dependencies>`:

    ```xml
    <dependency>
        <groupId>org.apache.qpid</groupId>
        <artifactId>qpid-jms-client</artifactId>
        <version>0.43.0</version>
    </dependency>
    ```

    ![project-pom3](https://github.com/bqchen/Spring-JMS-ServiceBus/raw/master/figure/project-pom3.jpg)

## Create a Plain Ordinary Java Object (POJO)

In this tutorial, the sample code refers to the official [Spring JMS starter project](https://spring.io/guides/gs/messaging-jms/). Let's first create a very simple POJO that embodies a welcome message. The welcome message is sent to all employees, which contains the name and id of the new employee joining the company. Note that we are not truly sending a welcome message. We’re simply sending the details from one place to another about WHAT to send in a message. The POJO is defined in:

`C:\Spring-JMS-ServiceBus\starter\src\main\java\example\Employee.java`

-or-

`/users/example/home/starter/src/main/java/example/Employee.java`

The sample code is shown as follows.

```java
package example;

import java.io.Serializable;

// Add Serializable
public class Employee implements Serializable{

    // Serializer ID
    private static final long serialVersionUID = -295422703255886286L;

    private String name;
    private String id;

    public Employee() {
    }

    public Employee(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("Welcome our new employee! {Name: %s, Id: %s}", getName(), getId());
    }

}
```

This POJO is quite simple, containing two fields, name and id, along with the presumed set of getters and setters. Here we implement `Serializable` in order to use the send methods in `JmsTemplate` in Spring framework correctly. Otherwise, a customized `MessageConverter` bean should be defined to serialize the content to json in text format (i.e. as a `TextMessage`). For more details about `MessageConverter`, see the official [Spring JMS starter project](https://spring.io/guides/gs/messaging-jms/).

## Send Spring JMS messages integrated with Azure Service Bus

Add a class with a **Main** method and then add the following `import` statements at the top of the Java file. In this article, `Application` class is created.

`C:\Spring-JMS-ServiceBus\starter\src\main\java\example\Application.java`

-or-

`/users/example/home/starter/src/main/java/example/Application.java`

```java
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
```

Update the `main()` method to send sample messages to the Service Bus queue or the Service Bus topic. The sample code refers to the official [Spring JMS starter project](https://spring.io/guides/gs/messaging-jms/).

```java
@SpringBootApplication
@EnableJms
public class Application {
    public static void main(String[] args) {
        // Launch the application
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

        // Send a message with a POJO
        System.out.println("Sending a welcome message.");
        jmsTemplate.convertAndSend(<DestinationName>, new Employee("exampleName", "10001"));
    }
}
```

`@SpringBootApplication` is a convenience annotation that adds all of the following:

- `@Configuration` tags the class as a source of bean definitions for the application context.

- `@EnableAutoConfiguration` tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.

- `@ComponentScan` tells Spring to look for other components, configurations, and services in the `example` package, allowing it to find the controllers.

`@EnableJms` triggers the discovery of methods annotated with `@JmsListener` in the receiver, creating the message listener container under the covers.

The `main()` method uses Spring Boot’s `SpringApplication.run()` method to launch an application. `JmsTemplate` makes it very simple to send messages to a JMS destination. In the main runner method, after starting things up, you can just use `jmsTemplate` to send an `Employee` POJO. Because the `Employee` POJO is `Serializable`, the message will be converted automatically.

> **Note**
> >
> &emsp; Replace `<DestinationName>` with your own queue name or topic name configured in your Service Bus namespace.

## Receive Spring JMS messages integrated with Azure Service Bus

### Define customized `ConnectionFactory`

Although `ConnectionFactory` can be created automatically by Spring Boot, when integrating Spring messaging with Azure Service Bus, a cutomized `ConnectionFactory` should be defined to configure the properties of your Service Bus namespace. In the `Application` class, add the following code before the `main()` method to define and configure your own `ConnectionFactory`.

```java
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
```

`@Bean` is a method-level annotation to register the return value as a bean within a `BeanFactory`. In this article, we have defined a `myConnectionFactory` bean that will be referenced in a `JmsListenerContainerFactory` instance.

`@Value` annotation indicates that the property value of the external configuration file named `application.properties` will be injected into the bean dynamically. In this tutorial, the property configuration for Azure Service Bus queues and Azure Service Bus topics in your Spring Boot application are slightly different. Since the `connectionString` is a key value to connect your application with Service Bus, you should configure your `connectionString` when you either use Azure Service Bus queues or Azure Service Bus topics. Note that since the JMS `clientId` is required in order to consume messages from topics, you need to configure your `clientId` when using Azure Service Bus topics and durable subscriptions.

You can configure your connection string (and client id) in `application.properties` file as follows.

1. Navigate to `application.properties` file in your application `resources` folder.

2. Open the file and add below property with your Service Bus connection string. For information about how to get a connection string, visit [Prerequisites](#Prerequisites). When using Service Bus topics and durable subscriptions, remember to add client id as below.

    ```properties
    spring.jms.servicebus.connection-string=<ServiceBusNamespaceConnectionString>
    spring.jms.servicebus.topic-client-id=<ServiceBusTopicClientId>
    ```

> **Note**
> >
> &emsp; Replace `<ServiceBusNamespaceConnectionString>` with the connection string of your Service Bus namespace.
> >
> &emsp; Replace `<ServiceBusTopicClientId>` with the client id for your Service Bus topic.

### Receive messages from a Service Bus queue

First, define a customized `JmsListenerContainerFactory` bean that will be referenced in the `JmsListener` annotation of the receiver. Referring to the official [Spring JMS starter project](https://spring.io/guides/gs/messaging-jms/), we define a `myQueueFactory` bean in order to receive messages from a Service Bus queue. In the `Application` class, add the following code before the `main()` method to define and configure your own `JmsListenerContainerFactory` for queue.

```java
@Bean
public JmsListenerContainerFactory<?> myQueueFactory(ConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    configurer.configure(factory, connectionFactory);
    return factory;
}
```

Second, define a message receiver. The sample code refers to the official [Spring JMS starter project](https://spring.io/guides/gs/messaging-jms/).

`C:\Spring-JMS-ServiceBus\starter\src\main\java\example\MessageReceiver.java`

-or-

`/users/example/home/starter/src/main/java/example/MessageReceiver.java`

```java
package example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

    // log4j logger
    private final Logger logger = LoggerFactory.getLogger(Receiver.class);

    @JmsListener(destination = <ServiceBusQueueName>, containerFactory = "myQueueFactory")
    public void receiveMessage(Employee employee) {
        logger.info("Receiving message from queue: {}", employee);
        System.out.println("Received from queue <" + employee + ">");
    }
}
```

As with `Employee`, `MessageReceiver` is also known as a **message driven POJO**. The `JmsListener` annotation defines the name of the `Destination`  (i.e, the name of the queue) that this method should listen to and the reference to the `JmsListenerContainerFactory` to use to create the underlying message listener container. Because we have customized the way the container is built, the last attribute is necessary. The `receiveMessage()` method receives messages from the queue and print logs on the screen.

> **Note**
> >
> &emsp; Replace `<ServiceBusQueueName>` with your own quene name configured in your Service Bus namespace.

### Receive messages from a Service Bus subscription

First, define a customized `JmsListenerContainerFactory` bean that will be referenced in the `JmsListener` annotation of the receiver. Referring to the official [Spring JMS starter project](https://spring.io/guides/gs/messaging-jms/), we define a `myTopicFactory` bean in order to receive messages from a Service Bus subscription. In the `Application` class, add the following code before the `main()` method to define and configure your own `JmsListenerContainerFactory` for subscription. Remember to create a **durable subscription**.

```java
@Bean
public JmsListenerContainerFactory<?> myTopicFactory(ConnectionFactory connectionFactory) {
    DefaultJmsListenerContainerFactory topicFactory = new DefaultJmsListenerContainerFactory();
    topicFactory.setConnectionFactory(connectionFactory);
    topicFactory.setSubscriptionDurable(Boolean.TRUE);
    return topicFactory;
}
```

Second, define a message receiver. Add the following code in

`C:\Spring-JMS-ServiceBus\starter\src\main\java\example\MessageReceiver.java`

-or-

`/users/example/home/starter/src/main/java/example/MessageReceiver.java`.

```java
@JmsListener(destination = <ServiceBusTopicName>, containerFactory = "myTopicFactory", subscription = <ServiceBusSubscriptionName>)
public void receiveTopicMessage(Employee employee) {
    logger.info("Receiving message from subscription: {}", employee);
    System.out.println("Received from subscription <" + employee + ">");
}
```

The `JmsListener` annotation defines the name of the `Destination`  (i.e, the name of the topic) that this method should listen to, the name of the `Subscription` as well as the reference to the `JmsListenerContainerFactory` to use to create the underlying message listener container. Because we have customized the way the container is built, the `containerFactory` value should be designated. The `receiveTopicMessage()` method receives messages from the subscription and print logs on the screen.

> **Note**
> >
> &emsp; Replace `<ServiceBusTopicName>` with your own topic name configured in your Service Bus namespace.
> >
> &emsp; Replace `<ServiceBusSubscriptionName>` with your own subscription name for your Service Bus topic.

## Run the program

If you use Service Bus queue, run the program to see the ouput similar to the following output:

```
Sending a welcome message.
Received from queue <Welcome our new employee! {Name: exampleName, Id: 10001}>
```

If you use Service Bus topic, run the program to see the output similar to the following ouput:

```
Sending a welcome message.
Received from subscription <Welcome our new employee! {Name: exampleName, Id: 10001}>
```
