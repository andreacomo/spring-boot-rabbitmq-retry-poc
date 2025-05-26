package it.codingjam.rabbimqretry.consumers;

import it.codingjam.rabbimqretry.consumers.dtos.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {

    private final JsonDeserializer deserializer;

    @RabbitListener(queues = "create_order_queue")
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
}
