package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AckFalse {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.queueDeclare("test_queue", false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                try {
                    processMessage(message);
                } catch (Exception e) {
                    System.out.println("Błąd: " + e);
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
