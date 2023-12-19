package org.example;

import ch.qos.logback.core.model.TimestampModel;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class AlternativeExchange {

    public static void declareExchange() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        channel.exchangeDeclare("alt.fanout.exchange", BuiltinExchangeType.FANOUT, true);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("alternate-exchange", "alt.fanout.exchange");
        channel.exchangeDeclare("alt.topic.exchange", BuiltinExchangeType.TOPIC, true, false, arguments);
        channel.close();
    }

    public static void declareQueues() throws IOException, TimeoutException {
        Channel chanel = ConnectionManager.getConnection().createChannel();

        // tworzymy kolejki
        chanel.queueDeclare("HealthQ", true, false, false, null);
        chanel.queueDeclare("SportsQ", true, false, false, null);
        chanel.queueDeclare("EducationQ", true, false, false, null);

        chanel.queueDeclare("FaultQueue", true, false, false, null);

        chanel.close();
    }

    public static void declareBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // Tworzenie linkow fanout kolejke, exchange, routingKey != null
        channel.queueBind("HealthQ", "alt.topic.exchange", "health.*");
        channel.queueBind("SportsQ", "alt.topic.exchange", "#.sports.*");
        channel.queueBind("EducationQ", "alt.topic.exchange", "#.education");

        channel.queueBind("FaultQueue", "alt.fanout.exchange", "");

        channel.close();
    }

    public static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        String message = "Learn something new everyday";
        channel.basicPublish("alt.topic.exchange", "education", null, message.getBytes());

        message = "Stay fit in Mind and Body";
        channel.basicPublish("alt.topic.exchange", "education.health", null, message.getBytes());

        channel.close();

    }

    public static void main(String[] args) throws IOException, TimeoutException {
        AlternativeExchange.declareQueues();
        AlternativeExchange.declareExchange();
        AlternativeExchange.declareBindings();

//        Thread subscribe = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    ExchangeToExchange.subscribeMessage();
//                } catch (IOException | TimeoutException e) {
//                    e.printStackTrace();
//                }
//            }
//        };

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    AlternativeExchange.publishMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

//        subscribe.start();
        publish.start();
    }

}
