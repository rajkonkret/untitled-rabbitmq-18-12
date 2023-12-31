package org.example;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ExchangeToExchange {
    public static void declareExchanges() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        channel.exchangeDeclare("linked-direct-exchange", BuiltinExchangeType.DIRECT, true);
        channel.exchangeDeclare("home-direct-exchange", BuiltinExchangeType.DIRECT, true);
        channel.close();
    }

    public static void declareQueues() throws IOException, TimeoutException {
        Channel chanel = ConnectionManager.getConnection().createChannel();

        // tworzymy kolejki
        chanel.queueDeclare("MobileQ", true, false, false, null);
        chanel.queueDeclare("ACQ", true, false, false, null);
        chanel.queueDeclare("LightQ", true, false, false, null);

        // Queue in home-direct-exchange
        chanel.queueDeclare("FanQ", true, false, false, null);
        chanel.queueDeclare("LaptopQ", true, false, false, null);

        chanel.close();
    }

    public static void declareQueueBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // Tworzenie linkow , kolejke, exchange, routingKey != null
        channel.queueBind("MobileQ", "linked-direct-exchange", "personalDevice");
        channel.queueBind("ACQ", "linked-direct-exchange", "homeAppliance");
        channel.queueBind("LightQ", "linked-direct-exchange", "homeAppliance");

        // home-direct-exchange
        channel.queueBind("FanQ", "home-direct-exchange", "homeAppliance");
        channel.queueBind("LaptopQ", "home-direct-exchange", "personalDevice");

        channel.close();
    }

    public static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        String message = "Direct message - Turn on the Home Appliance";
        channel.basicPublish("home-direct-exchange", "homeAppliance", null,
                message.getBytes());

        channel.close();
    }

    public static void declareExchangesBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.exchangeBind("linked-direct-exchange", "home-direct-exchange",
                "homeAppliance");
        channel.close();
    }

    public static void subscribeMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.basicConsume("MobileQ", true, ((consumerTag, message) -> {
            System.out.println("\n\n========= Mobile Queue ==========");
            System.out.println(consumerTag);
            System.out.println("MobileQ" + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("ACQ", true, ((consumerTag, message) -> {
            System.out.println("\n\n========= ACQ Queue ==========");
            System.out.println(consumerTag);
            System.out.println("ACQ" + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("LightQ", true, ((consumerTag, message) -> {
            System.out.println("\n\n========= Light Queue ==========");
            System.out.println(consumerTag);
            System.out.println("LightQ" + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("LaptopQ", true, ((consumerTag, message) -> {
            System.out.println("\n\n========= Laptop Queue ==========");
            System.out.println(consumerTag);
            System.out.println("LaptopQ" + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("FanQ", true, ((consumerTag, message) -> {
            System.out.println("\n\n========= FanQ Queue ==========");
            System.out.println(consumerTag);
            System.out.println("FanQ" + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        ExchangeToExchange.declareQueues();
        ExchangeToExchange.declareExchanges();
        ExchangeToExchange.declareQueueBindings();
        ExchangeToExchange.declareExchangesBindings();

        Thread subscribe = new Thread() {
            @Override
            public void run() {
                try {
                    ExchangeToExchange.subscribeMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    ExchangeToExchange.publishMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

        subscribe.start();
        publish.start();
    }

}
