package org.example;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TopicExchange {
    public static void declareExchange() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        channel.exchangeDeclare("my-topic-exchange", BuiltinExchangeType.TOPIC, true);
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
        channel.queueBind("HealthQ", "my-topic-exchange", "health.*");
        channel.queueBind("SportsQ", "my-topic-exchange", "#.sports.*");
        channel.queueBind("EducationQ", "my-topic-exchange", "#.education");

        channel.close();
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


    public static void main(String[] args) throws IOException, TimeoutException {
        TopicExchange.declareQueue();
        TopicExchange.declareExchange();
        TopicExchange.declareBindings();

//        Thread subscribe = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    FanoutExchange.subscribeMessage();
//                } catch (IOException | TimeoutException e) {
//                    e.printStackTrace();
//                }
//            }
//        };

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    TopicExchange.publishMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

//        subscribe.start();
        publish.start();
    }
}
