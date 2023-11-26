package ru.ermolaev.services.data.actuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DataActuatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataActuatorApplication.class, args);
	}

}
