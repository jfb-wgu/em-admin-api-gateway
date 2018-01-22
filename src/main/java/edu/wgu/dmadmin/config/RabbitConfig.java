package edu.wgu.dmadmin.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRabbit
@EnableRetry
public class RabbitConfig {

    @Configuration
    public static class RabbitListener implements RabbitListenerConfigurer {

        @Bean
        public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
            return new MappingJackson2MessageConverter();
        }

        @Bean
        public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
            DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
            factory.setMessageConverter(consumerJackson2MessageConverter());
            return factory;
        }

        @Override
        public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
            registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
        }
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}