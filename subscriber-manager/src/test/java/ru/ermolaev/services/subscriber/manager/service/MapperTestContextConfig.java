package ru.ermolaev.services.subscriber.manager.service;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableConfigurationProperties
@ComponentScan({"ru.ermolaev.services.subscriber.manager.mapper"})
public class MapperTestContextConfig {
}
