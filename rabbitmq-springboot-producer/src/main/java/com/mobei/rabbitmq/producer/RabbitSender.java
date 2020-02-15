package com.mobei.rabbitmq.producer;

import com.mobei.rabbitmq.entity.Order;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class RabbitSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final RabbitTemplate.ConfirmCallback confirmCallback = (correlationData, ack, cause) -> {
        System.out.println("correlationData: " + correlationData
                + " ack: " + ack
                + " cause: " + cause);
        if (!ack) {
            System.out.println("异常处理...");
        }
    };

    private final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.out.println("replyCode: " + replyCode + "replyText: " + replyText + "exchange: " + exchange + "   routingKey: " + routingKey);
        }
    };

    public void send(Object msg, Map<String, Object> properties) throws Exception {
        MessageHeaders mhs = new MessageHeaders(properties);
        Message message = MessageBuilder.createMessage(msg, mhs);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        CorrelationData cd = new CorrelationData();
        cd.setId(UUID.randomUUID().toString());//ID:全局唯一
        rabbitTemplate.convertAndSend("exchange-1", "springboot.hello", message, cd);
//        rabbitTemplate.convertAndSend("exchange-1", "spring.hello", message, cd);
    }

    public void sendOrder(Order order) throws Exception {
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        CorrelationData cd = new CorrelationData();
        cd.setId(UUID.randomUUID().toString());//ID:全局唯一
        rabbitTemplate.convertAndSend("exchange-2", "springboot.def", order, cd);
//        rabbitTemplate.convertAndSend("exchange-1", "spring.hello", message, cd);
    }

}
