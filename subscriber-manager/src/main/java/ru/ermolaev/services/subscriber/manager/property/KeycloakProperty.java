package ru.ermolaev.services.subscriber.manager.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperty implements ResourceNameProvider {

    private final String resourceName;

    @ConstructorBinding
    public KeycloakProperty(String resourceName) {
        this.resourceName = resourceName;
    }

}
