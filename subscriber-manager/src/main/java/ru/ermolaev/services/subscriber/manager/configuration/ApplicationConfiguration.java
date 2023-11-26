package ru.ermolaev.services.subscriber.manager.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.ermolaev.services.subscriber.manager.property.ApplicationProperty;
import ru.ermolaev.services.subscriber.manager.property.KeycloakProperty;

@Configuration
@EnableConfigurationProperties({
        ApplicationProperty.class,
        KeycloakProperty.class
})
public class ApplicationConfiguration {
}
