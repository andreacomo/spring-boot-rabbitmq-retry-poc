package it.codingjam.rabbimqretry.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConsumerConfig {

    public static final String CREATE_ORDER_QUEUE = "create_order_queue";
    public static final String ORDERS_DLQ = "orders_dlq";
    public static final String ORDERS_EXCHANGE = "orders-exchange";
    public static final String ORDERS_DLE = "orders-dle";
    public static final String ORDER_CREATED_RK = "order.created";

    @Bean
    public Queue createOrderQueue() {
        return QueueBuilder.durable(CREATE_ORDER_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDERS_DLE)
                .build();
    }

    @Bean
    public Queue ordersDlq() {
        return QueueBuilder.durable(ORDERS_DLQ)
                .build();
    }

    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(ORDERS_EXCHANGE);
    }

    @Bean
    public DirectExchange ordersDeadLetterExchange() {
        return new DirectExchange(ORDERS_DLE);
    }

    @Bean
    public Binding createOrderQueueBinding(Queue createOrderQueue, DirectExchange ordersExchange) {
        return BindingBuilder.bind(createOrderQueue).to(ordersExchange).with(ORDER_CREATED_RK);
    }

    @Bean
    public Binding ordersDlqBinding(Queue ordersDlq, DirectExchange ordersDeadLetterExchange) {
        return BindingBuilder.bind(ordersDlq).to(ordersDeadLetterExchange).with("");
    }
}
