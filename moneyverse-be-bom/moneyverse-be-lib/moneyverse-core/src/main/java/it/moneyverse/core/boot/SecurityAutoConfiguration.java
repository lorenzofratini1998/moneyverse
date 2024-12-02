package it.moneyverse.core.boot;

import it.moneyverse.core.security.converter.KeycloakJwtAuthenticationConverter;
import it.moneyverse.core.security.converter.KeycloakJwtRolesConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityAutoConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
      @Value("${spring.security.base-path}") String basePath) throws Exception {

    DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter = new DelegatingJwtGrantedAuthoritiesConverter(
        new JwtGrantedAuthoritiesConverter(),
        new KeycloakJwtRolesConverter()
    );

    KeycloakJwtAuthenticationConverter jwtAuthenticationConverter = new KeycloakJwtAuthenticationConverter(
        authoritiesConverter);

    httpSecurity
        .httpBasic(HttpBasicConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS
        ));

    httpSecurity.oauth2ResourceServer(oauth2 -> oauth2
        .jwt(
            jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)));

    httpSecurity
        .authorizeHttpRequests(authz -> authz
            .requestMatchers(basePath + "/public/**").permitAll()
            .anyRequest().authenticated()
        );

    return httpSecurity.build();
  }

}