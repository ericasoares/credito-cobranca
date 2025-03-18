package tech.ericasoares.credito_cobranca.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "cobranca.exchange";
    public static final String QUEUE_NAME = "cobranca.notificacao.queue";
    public static final String ROUTING_KEY = "cobranca.notificacao.routingKey";

    @Bean
    public Exchange cobrancaExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_NAME)
                .durable(true) // Exchange persistente
                .build();
    }

    @Bean
    public Queue cobrancaQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .build();
    }

    @Bean
    public Binding cobrancaBinding(Queue cobrancaQueue, Exchange cobrancaExchange) {
        return BindingBuilder.bind(cobrancaQueue)
                .to(cobrancaExchange)
                .with(ROUTING_KEY)
                .noargs();
    }
}