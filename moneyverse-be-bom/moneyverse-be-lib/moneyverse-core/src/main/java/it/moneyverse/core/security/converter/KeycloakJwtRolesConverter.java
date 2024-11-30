package it.moneyverse.core.security.converter;

import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_REALM_ACCESS;
import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_RESOURCE_ACCESS;
import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_ROLES;
import static it.moneyverse.core.utils.constants.SecurityConstants.PREFIX_ROLE;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakJwtRolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  @Override
  public Collection<GrantedAuthority> convert(@Nonnull Jwt jwt) {
    return Stream.concat(
        extractRealmRoles(jwt).stream(),
        extractResourceRoles(jwt).stream()
    ).toList();
  }

  private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
    return Optional.ofNullable(jwt.<Map<String, Collection<String>>>getClaim(CLAIM_REALM_ACCESS))
        .map(realmAccess -> realmAccess.get(CLAIM_ROLES))
        .stream()
        .flatMap(Collection::stream)
        .map(role -> new SimpleGrantedAuthority(PREFIX_ROLE + role))
        .collect(Collectors.toList());
  }

  private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
    return Optional.ofNullable(
            jwt.<Map<String, Map<String, Collection<String>>>>getClaim(CLAIM_RESOURCE_ACCESS))
        .stream()
        .flatMap(resourceAccess -> resourceAccess.entrySet().stream())
        .flatMap(entry -> Optional.ofNullable(entry.getValue().get(CLAIM_ROLES))
            .stream()
            .flatMap(Collection::stream)
            .map(role -> new SimpleGrantedAuthority(PREFIX_ROLE + entry.getKey() + "_" + role)))
        .collect(Collectors.toList());
  }

}
