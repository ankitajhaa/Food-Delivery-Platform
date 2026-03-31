package com.fooddelivery.deliveryservice.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String DELIVERY_EXCHANGE = "delivery.exchange";
    public static final String RIDER_QUEUE = "delivery.queue";
    public static final String DELIVERY_ROUTING_KEY = "delivery.created";

    @Bean public TopicExchange deliveryExchange() {
        return new TopicExchange(DELIVERY_EXCHANGE, true, false);
    }

    @Bean public Queue riderQueue() {
        return QueueBuilder.durable(RIDER_QUEUE).build();
    }

    @Bean public Binding riderBinding(Queue riderQueue, TopicExchange deliveryExchange) {
        return BindingBuilder.bind(riderQueue).to(deliveryExchange).with(DELIVERY_ROUTING_KEY);
    }

    @Bean public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(messageConverter());
        return t;
    }
}
