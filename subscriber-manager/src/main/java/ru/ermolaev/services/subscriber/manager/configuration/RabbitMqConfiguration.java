package ru.ermolaev.services.subscriber.manager.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

	public static final String MIGRATION_EXCHANGE_NAME = "migration-exchange";

	public static final String NOTIFICATION_REQUEST_EXCHANGE_NAME = "notification-request-exchange";

	public static final String NOTIFICATION_RESULT_EXCHANGE_NAME = "notification-result-exchange";

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
	public DirectExchange migrationExchange() {
		return new DirectExchange(MIGRATION_EXCHANGE_NAME);
	}

	@Bean
	public TopicExchange notificationRequestExchange() {
		return new TopicExchange(NOTIFICATION_REQUEST_EXCHANGE_NAME);
	}

	@Bean
	public TopicExchange notificationResultExchange() {
		return new TopicExchange(NOTIFICATION_RESULT_EXCHANGE_NAME);
	}

	@Bean
	public Queue notificationResultQueue() {
		return new Queue("notification-subs-result-queue");
	}

	@Bean
	public Binding notificationResultBinding() {
		return BindingBuilder.bind(notificationResultQueue())
				.to(notificationResultExchange())
				.with("notification.subs.result");
	}

}
