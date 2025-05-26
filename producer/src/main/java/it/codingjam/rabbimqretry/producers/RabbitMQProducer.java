package it.codingjam.rabbimqretry.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static it.codingjam.rabbimqretry.config.RabbitMQProducerConfig.ORDERS_EXCHANGE;
import static it.codingjam.rabbimqretry.config.RabbitMQProducerConfig.ORDER_CREATED_RK;

@Component
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    private final JsonSerializer serializer;

    public <T> void sendMessage(T data) throws JsonProcessingException {
        Message message = new Message(serializer.toByteArray(data));
        rabbitTemplate.send(ORDERS_EXCHANGE, ORDER_CREATED_RK, message);
    }
}
