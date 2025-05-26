package it.codingjam.rabbimqretry.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQProducerConfig {

    public static final String ORDERS_EXCHANGE = "orders-exchange";
    public static final String ORDER_CREATED_RK = "order.created";

    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(ORDERS_EXCHANGE);
    }
}
