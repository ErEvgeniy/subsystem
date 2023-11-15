package ru.ermolaev.services.subscriber.manager.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Locale;

@Getter
@ConfigurationProperties(prefix = "application")
public class ApplicationProperty implements LocaleProvider {

    private final Locale locale;

    @ConstructorBinding
    public ApplicationProperty(Locale locale) {
        this.locale = locale;
    }

}
