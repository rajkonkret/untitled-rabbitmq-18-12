package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class AckFalse {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");

        try (Connection connection = factory.newConnection(CommonConfig.AMQP_URL); Channel channel = connection.createChannel()) {
            channel.queueDeclare("test_queue", false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Otrzymano " + message);
                try {
                    processMessage(message);
                } catch (Exception e) {
                    System.out.println("Błąd: " + e.getMessage());
                    channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
                    return;
                }
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            };
            boolean autoAck = false;
            channel.basicConsume("test_queue", autoAck, deliverCallback, consumerTag -> {
            });

        }
    }

    private static void processMessage(String message) throws Exception {
        System.out.println("Przetwwarzanie wiadomości" + message);
        Thread.sleep(5000);
    }
}
