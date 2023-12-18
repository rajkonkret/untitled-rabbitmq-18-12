package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MessagePublisher {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection(CommonConfig.AMQP_URL);
        Channel channel = connection.createChannel();

        channel.queueDeclare(CommonConfig.DEAFAULT_QUEUE, true, false, false, null);
        for (int i = 0; i < 4; i++) {
            String message = "Getting started with rabbitMQ - Msg" + i;
            channel.basicPublish("", CommonConfig.DEAFAULT_QUEUE, null, message.getBytes());

        }
        channel.close();
        connection.close();
    }
}