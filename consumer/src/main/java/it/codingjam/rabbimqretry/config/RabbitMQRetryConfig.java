package it.codingjam.rabbimqretry.config;

import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.CompositeRetryPolicy;
import org.springframework.retry.policy.PredicateRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.Set;

import static it.codingjam.rabbimqretry.config.RabbitMQConsumerConfig.ORDERS_DLE;

@Configuration
public class RabbitMQRetryConfig {

    @Bean
    public ContainerCustomizer<SimpleMessageListenerContainer> customizer(
            RabbitProperties rabbitProperties,
            RabbitTemplate rabbitTemplate
    ) {
        RabbitProperties.ListenerRetry retryProperties = rabbitProperties.getListener().getSimple().getRetry();
        RetryPolicy simpleRetryPolicy = getRetryPolicy(retryProperties);
        return container -> container.setAdviceChain(RetryInterceptorBuilder.stateless()
                .retryPolicy(simpleRetryPolicy)
                .backOffOptions(
                        retryProperties.getInitialInterval().toMillis(),
                        retryProperties.getMultiplier(),
                        retryProperties.getMaxInterval().toMillis())
                .recoverer(new RepublishMessageRecoverer(rabbitTemplate, ORDERS_DLE, ""))
                .build());
    }

    private static RetryPolicy getRetryPolicy(RabbitProperties.ListenerRetry retryProperties) {
        Set<Class<IllegalArgumentException>> skipRetryFor = Set.of(IllegalArgumentException.class);
        PredicateRetryPolicy predicateRetryPolicy = new PredicateRetryPolicy(e -> !skipRetryFor.contains(e.getClass()) && !skipRetryFor.contains(e.getCause().getClass()));
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(retryProperties.getMaxAttempts());

        CompositeRetryPolicy compositeRetryPolicy = new CompositeRetryPolicy();
        compositeRetryPolicy.setPolicies(new RetryPolicy[] {simpleRetryPolicy, predicateRetryPolicy});
        return compositeRetryPolicy;
    }
}
