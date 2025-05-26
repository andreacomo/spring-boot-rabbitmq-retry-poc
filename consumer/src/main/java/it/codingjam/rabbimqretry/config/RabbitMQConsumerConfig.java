package it.codingjam.rabbimqretry.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConsumerConfig {

    public static final String CREATE_ORDER_QUEUE = "create-order-queue";
    public static final String APP_DLQ = "app-dlq";
    public static final String ORDERS_EXCHANGE = "orders-exchange";
    public static final String APP_DLX = "app-dead-letter-exchange";
    public static final String ORDER_CREATED_RK = "order.created";

    @Bean
    public Queue createOrderQueue() {
        return QueueBuilder.durable(CREATE_ORDER_QUEUE)
                .withArgument("x-dead-letter-exchange", APP_DLX)
                .build();
    }

    @Bean
    public Queue appDlq() {
        return QueueBuilder.durable(APP_DLQ)
                .build();
    }

    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(ORDERS_EXCHANGE);
    }

    @Bean
    public DirectExchange appDeadLetterExchange() {
        return new DirectExchange(APP_DLX);
    }

    @Bean
    public Binding createOrderQueueBinding(Queue createOrderQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(createOrderQueue).to(ordersExchange).with(ORDER_CREATED_RK);
    }

    @Bean
    public Binding appDlqBinding(Queue appDlq, DirectExchange appDeadLetterExchange) {
        return BindingBuilder.bind(appDlq).to(appDeadLetterExchange).with("");
    }
}
