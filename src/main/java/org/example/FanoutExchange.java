package org.example;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FanoutExchange {
    public static void declareExchange() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        channel.exchangeDeclare("my-fanout-exchange", BuiltinExchangeType.FANOUT, true);
        channel.close();
    }

    public static void declareQueue() throws IOException, TimeoutException {
        Channel chanel = ConnectionManager.getConnection().createChannel();

        // tworzymy kolejki
        chanel.queueDeclare("MobileQ", true, false, false, null);
        chanel.queueDeclare("ACQ", true, false, false, null);
        chanel.queueDeclare("LightQ", true, false, false, null);

        chanel.close();
    }

    public static void declareBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // Tworzenie linkow fanout kolejke, exchange, routingKey != null
        channel.queueBind("MobileQ", "my-fanout-exchange", "");
        channel.queueBind("ACQ", "my-fanout-exchange", "");
        channel.queueBind("LightQ", "my-fanout-exchange", "");

        channel.close();
    }

    public static void subscribeMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.basicConsume("LightQ", true, ((consumerTag, message) -> {
            System.out.println(consumerTag);
            System.out.println("LightQ" + new String(message.getBody()));
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("ACQ", true, ((consumerTag, message) -> {
            System.out.println(consumerTag);
            System.out.println("ACQ" + new String(message.getBody()));
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("MobileQ", true, ((consumerTag, message) -> {
            System.out.println(consumerTag);
            System.out.println("MobileQ" + new String(message.getBody()));
        }), consumerTag -> {
            System.out.println(consumerTag);
        });
    }

    public static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        String message = "Main Power is ON";
        channel.basicPublish("my-fanout-exchange", "", null, message.getBytes());

        channel.close();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        FanoutExchange.declareQueue();
        FanoutExchange.declareExchange();
        FanoutExchange.declareBindings();

        Thread subscribe = new Thread() {
            @Override
            public void run() {
                try {
                    FanoutExchange.subscribeMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    FanoutExchange.publishMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

        subscribe.start();
        publish.start();
    }
}