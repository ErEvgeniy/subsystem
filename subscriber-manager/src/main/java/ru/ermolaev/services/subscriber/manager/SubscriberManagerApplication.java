package ru.ermolaev.services.subscriber.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SubscriberManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriberManagerApplication.class, args);
	}

}
