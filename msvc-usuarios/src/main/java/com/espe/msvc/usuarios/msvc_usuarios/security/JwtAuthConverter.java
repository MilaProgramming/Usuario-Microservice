package com.espe.msvc.usuarios.msvc_usuarios.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    public static final String CLAIMS = "claims";
    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private static Collection<? extends GrantedAuthority> extractResourceRoles(final Jwt jwt) throws JsonProcessingException {
        Set<GrantedAuthority> resourcesRoles = new HashSet<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        resourcesRoles.addAll(extractResourceAccess(objectMapper.readTree(objectMapper.writeValueAsString(jwt)).get(CLAIMS)));
        resourcesRoles.addAll(extractRealAccess(objectMapper.readTree(objectMapper.writeValueAsString(jwt)).get(CLAIMS)));
        resourcesRoles.addAll(extractScope(objectMapper.readTree(objectMapper.writeValueAsString(jwt)).get(CLAIMS)));
        System.out.println("Authorities = " + resourcesRoles);
        return resourcesRoles;
    }


    private static List<GrantedAuthority> extractResourceAccess(JsonNode jwt) {
        Set<String> rolesWithPrefix = new HashSet<>();

        jwt.path("resource_access")
                .elements()
                .forEachRemaining(e -> e.path("roles")
                        .elements()
                        .forEachRemaining(r -> rolesWithPrefix.add("ROLE_" + r.asText()))
                );

        return AuthorityUtils.createAuthorityList(rolesWithPrefix.toArray(new String[0]));
    }

    private static List<GrantedAuthority> extractRealAccess(JsonNode jwt) {
        Set<String> rolesWithPrefix = new HashSet<>();

        jwt.path("realm_access")
                .elements()
                .forEachRemaining(e -> e.elements()
                        .forEachRemaining(r -> rolesWithPrefix.add("ROLE_" + r.asText()))
                );

        return AuthorityUtils.createAuthorityList(rolesWithPrefix.toArray(new String[0]));
    }

    private static List<GrantedAuthority> extractScope(JsonNode jwt) {
        Set<String> rolesWithPrefix = new HashSet<>();
        String scope = jwt.path("scope").toString().substring(1, jwt.path("scope").toString().length() - 1);
        Arrays.stream(scope.split(" ")).forEach(
                e -> rolesWithPrefix.add("SCOPE_" + e)
        );

        return AuthorityUtils.createAuthorityList(rolesWithPrefix.toArray(new String[0]));
    }

    public AbstractAuthenticationToken convert(final Jwt source) {
        Collection<GrantedAuthority> authorities = null;
        try {
            authorities = this.getGrantedAuthorities(source);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new JwtAuthenticationToken(source, authorities);
    }

    public Collection<GrantedAuthority> getGrantedAuthorities(Jwt source) throws JsonProcessingException {
        return Stream.concat(this.defaultGrantedAuthoritiesConverter.convert(source).stream(),
                extractResourceRoles(source).stream()).collect(Collectors.toSet());
    }
}
