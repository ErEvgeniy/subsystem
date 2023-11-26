package ru.ermolaev.services.subscriber.manager.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import ru.ermolaev.services.subscriber.manager.property.ResourceNameProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String USERNAME_ATTRIBUTE = "preferred_username";

    private static final String ROLE_PREFIX = "ROLE_";

    private final ResourceNameProvider resourceNameProvider;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Set<GrantedAuthority> authorities = Stream.concat(
                extractRealmRoles(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, jwt.getClaim(USERNAME_ATTRIBUTE));
    }

    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null || !(realmAccess.get("roles") instanceof Collection<?>)) {
            return Collections.emptySet();
        }

        Collection<String> realmRoles = ((Collection<String>) realmAccess.get("roles"));

        return buildGrantedAuthorities(realmRoles);
    }


    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null
                || !(resourceAccess.get(resourceNameProvider.getResourceName()) instanceof Map<?, ?>)) {
            return Collections.emptySet();
        }

        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get("sub-manager");

        if (resource == null || !(resource.get("roles") instanceof Collection<?>)) {
            return Collections.emptySet();
        }

        Collection<String> resourceRoles = (Collection<String>) resource.get("roles");

        return buildGrantedAuthorities(resourceRoles);
    }

    private Collection<? extends GrantedAuthority> buildGrantedAuthorities(Collection<String> plainRoles) {
        return plainRoles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .collect(Collectors.toSet());
    }

}
