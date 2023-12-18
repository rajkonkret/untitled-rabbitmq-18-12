package org.example;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel chanel = connection.createChannel()) {

            chanel.queueDeclare(QUEUE_NAME, false, false, false, null);

            String message = "Hello, RabbitMQ";

            chanel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [X] Sent '" + message + "'");

        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}