package it.codingjam.rabbimqretry.consumers;

import it.codingjam.rabbimqretry.config.RabbitMQConsumerConfig;
import it.codingjam.rabbimqretry.consumers.dtos.OrderDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderConsumer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderConsumer.class);

    private final JsonDeserializer deserializer;

    private final DqlRepublishMessageRecoverer recoverer;

    public OrderConsumer(JsonDeserializer deserializer, RabbitTemplate rabbitTemplate) {
        this.deserializer = deserializer;
        this.recoverer = new DqlRepublishMessageRecoverer(rabbitTemplate, RabbitMQConsumerConfig.APP_DLX, "");
    }

    @Retryable(
            retryFor = {RuntimeException.class},
            noRetryFor = {IllegalArgumentException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2, maxDelay = 60000)
    )
    @RabbitListener(queues = RabbitMQConsumerConfig.CREATE_ORDER_QUEUE)
    public void listen(Message message) {
        log.info("Received message: {}", message);
        OrderDto order = deserializer.fromByteArray(message.getBody(), OrderDto.class);
        log.info("Order received: {}", order);
        if (order.price().compareTo(BigDecimal.ZERO) < 0) {
            log.error("Price cannot be negative. Skipping message");
            throw new IllegalArgumentException("Price cannot be negative. Skipping message");
        } else if (order.price().equals(BigDecimal.ZERO)) {
            log.error("Price cannot be zero. I will retry just for test");
            throw new RuntimeException("Price cannot be zero. I will retry just for test");
        }
    }


    @Recover
    public void recover(Exception e, Message message) {
        log.error("Error processing message: {}, error: {}", message, e.getMessage());
        recoverer.recover(message, e);
    }
}
