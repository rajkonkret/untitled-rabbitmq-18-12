package org.example;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
        channel.queueBind("SportsQ", "my-header-exchange", "", sportsArgs);

        Map<String, Object> educationArgs = new HashMap<>();

        educationArgs.put("x-match", "any");
        educationArgs.put("h1", "Header1");
        educationArgs.put("h2", "Header2");
        channel.queueBind("EducationQ", "my-header-exchange", "", educationArgs);

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

        String message = "Headers exchange example 1";
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("h1", "Header1");
        headerMap.put("h2", "Header2");
        BasicProperties properties = new BasicProperties()
                .builder().headers(headerMap).build();
        channel.basicPublish("my-header-exchange", "", properties, message.getBytes());

        message = "Headers exchange example 2";
        Map<String, Object> headerMap2 = new HashMap<>();

        headerMap2.put("h2", "Header2");
        properties = new BasicProperties()
                .builder().headers(headerMap2).build();
        channel.basicPublish("my-header-exchange", "", properties, message.getBytes());

        message = "Headers exchange example 3";
        Map<String, Object> headerMap3 = new HashMap<>();

        headerMap3.put("h3", "Headers3");
        properties = new BasicProperties()
                .builder().headers(headerMap3).build();
        channel.basicPublish("my-header-exchange2", "", properties, message.getBytes());

        channel.close();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        HeadersExchange.declareQueue();
        HeadersExchange.declareExchange();
        HeadersExchange.declareBindings();

        Thread subscribe = new Thread() {
            @Override
            public void run() {
                try {
                    HeadersExchange.subscribeMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    HeadersExchange.publishMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

        subscribe.start();
        publish.start();
    }
}
