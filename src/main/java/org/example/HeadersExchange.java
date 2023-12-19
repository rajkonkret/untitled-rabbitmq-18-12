package org.example;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class HeadersExchange {
    public static void declareExchange() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        channel.exchangeDeclare("my-header-exchange", BuiltinExchangeType.HEADERS, true);
        channel.close();
    }

    public static void declareQueue() throws IOException, TimeoutException {
        Channel chanel = ConnectionManager.getConnection().createChannel();

        // tworzymy kolejki
        chanel.queueDeclare("HealthQ", true, false, false, null);
        chanel.queueDeclare("SportsQ", true, false, false, null);
        chanel.queueDeclare("EducationQ", true, false, false, null);

        chanel.close();
    }

    public static void declareBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // Tworzenie linkow fanout kolejke, exchange, routingKey != null
        // {"arg" : "wartośc"}
        Map<String, Object> healthArgs = new HashMap<>();
        healthArgs.put("x-match", "any");  // any czyli or - lub
        healthArgs.put("h1", "Header1");
        healthArgs.put("h2", "Header2");
        channel.queueBind("HealthQ", "my-header-exchange", "", healthArgs);

        Map<String, Object> sportsArgs = new HashMap<>();
        sportsArgs.put("x-match", "all");  // all czyli and - i
        sportsArgs.put("h1", "Header1");
        sportsArgs.put("h2", "Header2");
        channel.queueBind("SportsQ", "my-topic-exchange", "", sportsArgs);

        Map<String, Object> educationArgs = new HashMap<>();

        educationArgs.put("x-match", "any");
        educationArgs.put("h1", "Header1");
        educationArgs.put("h2", "Header2");
        channel.queueBind("EducationQ", "my-topic-exchange", "", educationArgs);

        channel.close();
    }

    public static void subscribeMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.basicConsume("HealthQ", true, ((consumerTag, message) -> {
            System.out.println("\n\n========= Health Queue ==========");
            System.out.println(consumerTag);
            System.out.println("HealthQ" + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("SportsQ", true, ((consumerTag, message) -> {
            System.out.println("\n\n========= Sports Queue ==========");
            System.out.println(consumerTag);
            System.out.println("SportsQ" + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("EducationQ", true, ((consumerTag, message) -> {
            System.out.println("\n\n========= Education Queue ==========");
            System.out.println(consumerTag);
            System.out.println("EducationQ" + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });
    }

    public static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        String message = "Drink a lot of Water and stay Healthy";
        channel.basicPublish("my-topic-exchange", "health.education", null, message.getBytes());

        message = "Learn somthing new everyday";
        channel.basicPublish("my-topic-exchange", "education", null, message.getBytes());

        message = "Stay fit in Mind and Body";
        channel.basicPublish("my-topic-exchange", "education.health", null, message.getBytes());

        message = "Just do it";
        channel.basicPublish("my-topic-exchange", "sports.sports", null, message.getBytes());

        channel.close();
    }
}
