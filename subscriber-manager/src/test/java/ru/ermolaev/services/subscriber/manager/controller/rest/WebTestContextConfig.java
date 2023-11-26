package ru.ermolaev.services.subscriber.manager.controller.rest;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import ru.ermolaev.services.subscriber.manager.configuration.ApplicationConfiguration;
import ru.ermolaev.services.subscriber.manager.configuration.WebSecurityConfiguration;
import ru.ermolaev.services.subscriber.manager.converter.KeycloakJwtAuthenticationConverter;

@SpringBootConfiguration
@EnableConfigurationProperties
@Import({
        WebSecurityConfiguration.class,
        KeycloakJwtAuthenticationConverter.class,
        ApplicationConfiguration.class
})
public class WebTestContextConfig {

    @Bean
    public JwtDecoder mockJwtDecoder() {
        return (test) -> new Jwt(null, null, null, null, null);
    }

}
