package com.mobei.rabbitmq.consumer;

import com.mobei.rabbitmq.entity.Order;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RabbitReceiver {

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "queue-1", durable = "true"),
                    exchange = @Exchange(
                            value = "exchange-1",
                            durable = "true",
                            type = "topic",
                            ignoreDeclarationExceptions = "true"
                    ),
                    key = "springboot.*"
            )
    )
    @RabbitHandler
    public void onMessage(Message msg, Channel channel) throws Exception {
        System.out.println("消费端收到消息体内容Payload: " + msg.getPayload());
        Long deliveryTag = (Long) msg.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK
        channel.basicAck(deliveryTag, false);
    }

    /**
     * @param order
     * @param channel
     * @throws Exception
     * @Payload:指定实际的消息体内容
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}", durable = "${spring.rabbitmq.listener.order.queue.durable}"),
                    exchange = @Exchange(
                            value = "${spring.rabbitmq.listener.order.exchange.name}",
                            durable = "${spring.rabbitmq.listener.order.exchange.durable}",
                            type = "${spring.rabbitmq.listener.order.exchange.type}",
                            ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"
                    ),
                    key = "${spring.rabbitmq.listener.order.key}"
            )
    )
    @RabbitHandler
    public void onOrderMessage(@Payload Order order, Channel channel, @Headers Map<String, Object> headers) throws Exception {
        System.out.println("消费端order: " + order.getId());
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK
        channel.basicAck(deliveryTag, false);
    }

}
