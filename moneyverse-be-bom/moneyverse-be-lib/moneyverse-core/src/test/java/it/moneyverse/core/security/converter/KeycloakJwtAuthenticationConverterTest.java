package it.moneyverse.core.security.converter;

import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_EMAIL;
import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_EMAIL_VERIFIED;
import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_FAMILY_NAME;
import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_GIVEN_NAME;
import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_PREFERRED_USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import it.moneyverse.core.model.auth.AuthenticatedUser;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class KeycloakJwtAuthenticationConverterTest {

  @Test
  void testConvertValidJwt(
      @Mock Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter,
      @Mock Jwt jwt,
      @Mock GrantedAuthority authority) {
    KeycloakJwtAuthenticationConverter converter =
        new KeycloakJwtAuthenticationConverter(authoritiesConverter);

    when(jwt.getSubject()).thenReturn("user123");
    when(jwt.getClaimAsString(CLAIM_GIVEN_NAME)).thenReturn("John");
    when(jwt.getClaimAsString(CLAIM_FAMILY_NAME)).thenReturn("Doe");
    when(jwt.getClaimAsString(CLAIM_PREFERRED_USERNAME)).thenReturn("johndoe");
    when(jwt.getClaimAsString(CLAIM_EMAIL)).thenReturn("johndoe@example.com");
    when(jwt.getClaimAsBoolean(CLAIM_EMAIL_VERIFIED)).thenReturn(true);

    when(authoritiesConverter.convert(jwt)).thenReturn(List.of(authority));

    UsernamePasswordAuthenticationToken token =
        (UsernamePasswordAuthenticationToken) converter.convert(jwt);

    assertNotNull(token);
    assertEquals(List.of(authority), token.getAuthorities());
    assertNull(token.getCredentials());

    AuthenticatedUser principal = (AuthenticatedUser) token.getPrincipal();
    assertEquals("user123", principal.getId());
    assertEquals("johndoe", principal.getName());
    assertEquals("John Doe", principal.getFullName());
    assertEquals("johndoe", principal.getUsername());
    assertEquals("johndoe@example.com", principal.getEmail());
    assertTrue(principal.getEmailVerified());
    assertEquals(List.of(authority), principal.getAuthorities());
  }

  @Test
  void testConvertHandlesEmptyAuthorities(
      @Mock Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter, @Mock Jwt jwt) {
    KeycloakJwtAuthenticationConverter converter =
        new KeycloakJwtAuthenticationConverter(authoritiesConverter);

    when(jwt.getSubject()).thenReturn("user123");
    when(jwt.getClaimAsString(CLAIM_GIVEN_NAME)).thenReturn("John");
    when(jwt.getClaimAsString(CLAIM_FAMILY_NAME)).thenReturn("Doe");
    when(jwt.getClaimAsString(CLAIM_PREFERRED_USERNAME)).thenReturn("johndoe");
    when(jwt.getClaimAsString(CLAIM_EMAIL)).thenReturn("johndoe@example.com");
    when(jwt.getClaimAsBoolean(CLAIM_EMAIL_VERIFIED)).thenReturn(true);

    when(authoritiesConverter.convert(jwt)).thenReturn(List.of());

    UsernamePasswordAuthenticationToken token =
        (UsernamePasswordAuthenticationToken) converter.convert(jwt);

    assertNotNull(token);
    assertTrue(token.getAuthorities().isEmpty());
    assertNull(token.getCredentials());

    AuthenticatedUser principal = (AuthenticatedUser) token.getPrincipal();
    assertEquals("user123", principal.getId());
    assertEquals("johndoe", principal.getName());
    assertEquals("John Doe", principal.getFullName());
    assertEquals("johndoe", principal.getUsername());
    assertEquals("johndoe@example.com", principal.getEmail());
    assertTrue(principal.getEmailVerified());
    assertTrue(principal.getAuthorities().isEmpty());
  }
}
