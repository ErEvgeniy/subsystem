package ru.ermolaev.services.notification.email.sender.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}

	@Bean
	public Jackson2JsonMessageConverter jsonConverter(ObjectMapper objectMapper) {
		return new Jackson2JsonMessageConverter(objectMapper);
	}

	@Bean
	public RabbitTemplate rabbitTemplate(
			ConnectionFactory connectionFactory,
			Jackson2JsonMessageConverter jsonConverter
	) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonConverter);
		return rabbitTemplate;
	}

	@Bean
	public Queue emailQueue() {
		return new Queue("email-queue");
	}

	@Bean
	public TopicExchange notificationRequestExchange() {
		return new TopicExchange("notification-request-exchange");
	}

	@Bean
	public Binding emailNotificationBinding() {
		return BindingBuilder.bind(emailQueue())
				.to(notificationRequestExchange())
				.with("notification.email");
	}

	@Bean
	public TopicExchange notificationResultExchange() {
		return new TopicExchange("notification-result-exchange");
	}

}
