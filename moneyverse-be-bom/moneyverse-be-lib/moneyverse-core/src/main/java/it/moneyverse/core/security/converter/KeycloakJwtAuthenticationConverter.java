package it.moneyverse.core.security.converter;

import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_EMAIL;
import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_EMAIL_VERIFIED;
import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_FAMILY_NAME;
import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_GIVEN_NAME;
import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_PREFERRED_USERNAME;

import it.moneyverse.core.model.auth.AuthenticatedUser;
import jakarta.annotation.Nonnull;
import java.security.Principal;
import java.util.Collection;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakJwtAuthenticationConverter
    implements Converter<Jwt, AbstractAuthenticationToken> {

  private final Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter;

  public KeycloakJwtAuthenticationConverter(
      Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter) {
    this.authoritiesConverter = authoritiesConverter;
  }

  @Override
  public AbstractAuthenticationToken convert(@Nonnull Jwt jwt) {
    Collection<GrantedAuthority> authorities = authoritiesConverter.convert(jwt);
    Principal principal =
        AuthenticatedUser.builder()
            .withId(jwt.getSubject())
            .withName(jwt.getClaimAsString(CLAIM_GIVEN_NAME))
            .withSurname(jwt.getClaimAsString(CLAIM_FAMILY_NAME))
            .withUsername(jwt.getClaimAsString(CLAIM_PREFERRED_USERNAME))
            .withEmail(jwt.getClaimAsString(CLAIM_EMAIL))
            .withIsEmailVerified(jwt.getClaimAsBoolean(CLAIM_EMAIL_VERIFIED))
            .withAuthorities(authorities)
            .build();
    return new UsernamePasswordAuthenticationToken(principal, null, authorities);
  }
}
