package ru.ermolaev.services.data.actuator.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

	@Bean
	public Queue migrationQueue() {
		return QueueBuilder.durable("migration-queue")
				.maxLength(2)
				.deadLetterExchange("dead-letter-exchange")
				.build();
	}

	@Bean
	public DirectExchange migrationExchange() {
		return new DirectExchange("migration-exchange");
	}

	@Bean
	public Binding migrationBinding() {
		return BindingBuilder.bind(migrationQueue())
				.to(migrationExchange())
				.with("migration");
	}

	@Bean
	public FanoutExchange deadLetterExchange() {
		return new FanoutExchange("dead-letter-exchange");
	}

	@Bean
	public Queue deadLetterQueue() {
		return new Queue("dead-letter-queue");
	}

	@Bean
	public Binding deadLetterBinding() {
		return BindingBuilder.bind(deadLetterQueue())
				.to(deadLetterExchange());
	}

}
