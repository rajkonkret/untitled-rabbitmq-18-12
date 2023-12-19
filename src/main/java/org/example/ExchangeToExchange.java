package org.example;

import ch.qos.logback.core.model.TimestampModel;
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

}
