package it.moneyverse.core.utils.properties;

import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = KeycloakProperties.KEYCLOAK_PREFIX)
public class KeycloakProperties {
    public static final String KEYCLOAK_PREFIX = "spring.security.oauth2.keycloak";
    public static final String KEYCLOAK_HOST = KEYCLOAK_PREFIX + ".host";
    public static final String KEYCLOAK_PORT = KEYCLOAK_PREFIX + ".port";
    public static final String KEYCLOAK_REALM = KEYCLOAK_PREFIX + ".realm-name";

    private final String host;
    private final Integer port;
    private final String realmName;
    private final String issuerUri;
    private final String jwkSetUri;

    @ConstructorBinding
    public KeycloakProperties(String host, Integer port, String realmName) {
        this.host = Objects.requireNonNull(host, KEYCLOAK_PREFIX + ".host cannot be null");
        this.port = Objects.requireNonNull(port, KEYCLOAK_PREFIX + ".port cannot be null");
        this.realmName = Objects.requireNonNull(realmName, KEYCLOAK_PREFIX + ".realm-name cannot be null");
        this.issuerUri = "http://%s:%s/realms/%s".formatted(host, port, realmName);
        this.jwkSetUri = "http://%s:%s/realms/%s/protocol/openid-connect/certs".formatted(host, port, realmName);
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getRealmName() {
        return realmName;
    }

    public String getIssuerUri() {
        return issuerUri;
    }

    public String getJwkSetUri() {
        return jwkSetUri;
    }
}
