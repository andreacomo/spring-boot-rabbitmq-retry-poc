package it.codingjam.rabbimqretry.config;

import org.springframework.amqp.core.Message;
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

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static it.codingjam.rabbimqretry.config.RabbitMQConsumerConfig.APP_DLX;

@Configuration
public class RabbitMQRetryConfig {

    private static final Set<Class<IllegalArgumentException>> SKIP_RETRY_FOR = Set.of(IllegalArgumentException.class);

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
                .recoverer(new DqlRepublishMessageRecoverer(rabbitTemplate, APP_DLX, ""))
                .build());
    }

    private static RetryPolicy getRetryPolicy(RabbitProperties.ListenerRetry retryProperties) {
        PredicateRetryPolicy predicateRetryPolicy = new PredicateRetryPolicy(e -> !SKIP_RETRY_FOR.contains(e.getClass()) && !SKIP_RETRY_FOR.contains(e.getCause().getClass()));
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(retryProperties.getMaxAttempts());

        CompositeRetryPolicy compositeRetryPolicy = new CompositeRetryPolicy();
        compositeRetryPolicy.setPolicies(new RetryPolicy[] {simpleRetryPolicy, predicateRetryPolicy});

        return compositeRetryPolicy;
    }

    static class DqlRepublishMessageRecoverer extends RepublishMessageRecoverer {

        public static final String X_ORIGINAL_CONSUMER_Q = "x-original-consumerQueue";

        private static final String X_RETRY_EXHAUSTED_AT = "x-retry-exhaustedAt";

        DqlRepublishMessageRecoverer(RabbitTemplate rabbitTemplate, String exchange, String routingKey) {
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
}
