package it.codingjam.rabbimqretry.consumers;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;

import java.time.Instant;
import java.util.Map;

public class DqlRepublishMessageRecoverer extends RepublishMessageRecoverer {

    private static final String X_ORIGINAL_CONSUMER_Q = "x-original-consumerQueue";
    private static final String X_RETRY_EXHAUSTED_AT = "x-retry-exhaustedAt";

    public DqlRepublishMessageRecoverer(RabbitTemplate rabbitTemplate, String exchange, String routingKey) {
        super(rabbitTemplate, exchange, routingKey);
    }

    @Override
    protected Map<? extends String, ?> additionalHeaders(Message message, Throwable cause) {
        return Map.of(
                X_ORIGINAL_CONSUMER_Q, message.getMessageProperties().getConsumerQueue(),
                X_RETRY_EXHAUSTED_AT, Instant.now().toString()
        );
    }
}
