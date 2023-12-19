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
}
