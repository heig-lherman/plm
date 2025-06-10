package ch.vd.ptep.mrq.engine.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration(proxyBeanMethods = false)
public class JmsConfig {

    public static final String REVIEW_QUEUE = "review-moderation-queue";

    @Bean
    public MessageConverter messageConverter(
        ObjectMapper objectMapper
    ) {
        var converter = new MappingJackson2MessageConverter(objectMapper);
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public JmsTemplate jmsTemplate(
        ConnectionFactory connectionFactory,
        MessageConverter messageConverter
    ) {
        var jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(messageConverter);
        jmsTemplate.setDefaultDestinationName(REVIEW_QUEUE);
        return jmsTemplate;
    }

}
