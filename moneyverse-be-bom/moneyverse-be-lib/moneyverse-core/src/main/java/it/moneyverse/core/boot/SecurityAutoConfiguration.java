package it.moneyverse.core.boot;

import it.moneyverse.core.security.converter.KeycloakJwtAuthenticationConverter;
import it.moneyverse.core.security.converter.KeycloakJwtRolesConverter;
import it.moneyverse.core.services.SecurityService;
import it.moneyverse.core.utils.SecurityContextUtils;
import it.moneyverse.core.utils.properties.KeycloakProperties;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
@EnableWebSecurity
@EnableMethodSecurity
@EnableJpaAuditing
public class SecurityAutoConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAutoConfiguration.class);
  private final KeycloakProperties keycloakProperties;

  public SecurityAutoConfiguration(KeycloakProperties keycloakProperties) {
    this.keycloakProperties = keycloakProperties;
    LOGGER.info("Starting to load beans from {}", SecurityAutoConfiguration.class);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

    DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter =
        new DelegatingJwtGrantedAuthoritiesConverter(
            new JwtGrantedAuthoritiesConverter(), new KeycloakJwtRolesConverter());

    KeycloakJwtAuthenticationConverter jwtAuthenticationConverter =
        new KeycloakJwtAuthenticationConverter(authoritiesConverter);

    httpSecurity
        .httpBasic(HttpBasicConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    httpSecurity.oauth2ResourceServer(
        oauth2 ->
            oauth2.jwt(
                jwtConfigurer -> {
                  jwtConfigurer.jwkSetUri(keycloakProperties.getJwkSetUri());
                  jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter);
                }));

    httpSecurity.authorizeHttpRequests(
        authz -> authz.requestMatchers("/public/**").permitAll().anyRequest().authenticated());

    return httpSecurity.build();
  }

  @Bean
  public AuditorAware<String> auditorAware() {
    return () -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !authentication.isAuthenticated()) {
        return Optional.empty();
      }
      return Optional.of((SecurityContextUtils.getAuthenticatedUser().getUsername()));
    };
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return JwtDecoders.fromIssuerLocation(keycloakProperties.getIssuerUri());
  }

  @Bean
  public SecurityService securityService() {
    return new SecurityService();
  }
}
