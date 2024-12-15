package it.moneyverse.core.security.converter;

import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_REALM_ACCESS;
import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_RESOURCE_ACCESS;
import static it.moneyverse.core.utils.constants.SecurityConstants.CLAIM_ROLES;
import static it.moneyverse.core.utils.constants.SecurityConstants.PREFIX_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class KeycloakJwtRolesConverterTest {

  private KeycloakJwtRolesConverter converter;

  @BeforeEach
  void setUp() {
    converter = new KeycloakJwtRolesConverter();
  }

  @Test
  void testConvertWithRealmRoles(@Mock Jwt jwt) {
    Map<String, Collection<String>> realmAccess = Map.of(CLAIM_ROLES, List.of("admin", "user"));
    when(jwt.getClaim(CLAIM_REALM_ACCESS)).thenReturn(realmAccess);

    Collection<GrantedAuthority> authorities = converter.convert(jwt);

    assertNotNull(authorities);
    assertEquals(2, authorities.size());
    assertTrue(authorities.contains(new SimpleGrantedAuthority(PREFIX_ROLE + "admin")));
    assertTrue(authorities.contains(new SimpleGrantedAuthority(PREFIX_ROLE + "user")));
  }

  @Test
  void testConvertWithResourceRoles(@Mock Jwt jwt) {
    Map<String, Map<String, Collection<String>>> resourceAccess =
        Map.of(
            "resource1", Map.of(CLAIM_ROLES, List.of("viewer", "editor")),
            "resource2", Map.of(CLAIM_ROLES, List.of("manager")));
    when(jwt.getClaim(CLAIM_REALM_ACCESS)).thenReturn(null);
    when(jwt.getClaim(CLAIM_RESOURCE_ACCESS)).thenReturn(resourceAccess);

    Collection<GrantedAuthority> authorities = converter.convert(jwt);

    assertNotNull(authorities);
    assertEquals(3, authorities.size());
    assertTrue(authorities.contains(new SimpleGrantedAuthority(PREFIX_ROLE + "resource1_viewer")));
    assertTrue(authorities.contains(new SimpleGrantedAuthority(PREFIX_ROLE + "resource1_editor")));
    assertTrue(authorities.contains(new SimpleGrantedAuthority(PREFIX_ROLE + "resource2_manager")));
  }

  @Test
  void testConvertWithRealmAndResourceRoles(@Mock Jwt jwt) {
    Map<String, Collection<String>> realmAccess = Map.of(CLAIM_ROLES, List.of("admin"));
    Map<String, Map<String, Collection<String>>> resourceAccess =
        Map.of("resource1", Map.of(CLAIM_ROLES, List.of("viewer")));
    when(jwt.getClaim(CLAIM_REALM_ACCESS)).thenReturn(realmAccess);
    when(jwt.getClaim(CLAIM_RESOURCE_ACCESS)).thenReturn(resourceAccess);

    Collection<GrantedAuthority> authorities = converter.convert(jwt);

    assertNotNull(authorities);
    assertEquals(2, authorities.size());
    assertTrue(authorities.contains(new SimpleGrantedAuthority(PREFIX_ROLE + "admin")));
    assertTrue(authorities.contains(new SimpleGrantedAuthority(PREFIX_ROLE + "resource1_viewer")));
  }

  @Test
  void testConvertWithNoRoles(@Mock Jwt jwt) {
    when(jwt.getClaim(CLAIM_REALM_ACCESS)).thenReturn(null);
    when(jwt.getClaim(CLAIM_RESOURCE_ACCESS)).thenReturn(null);

    Collection<GrantedAuthority> authorities = converter.convert(jwt);

    assertNotNull(authorities);
    assertTrue(authorities.isEmpty());
  }

  @Test
  void testConvertWithEmptyRoles(@Mock Jwt jwt) {
    Map<String, Collection<String>> realmAccess = Map.of(CLAIM_ROLES, List.of());
    Map<String, Map<String, Collection<String>>> resourceAccess = Map.of();
    when(jwt.getClaim(CLAIM_REALM_ACCESS)).thenReturn(realmAccess);
    when(jwt.getClaim(CLAIM_RESOURCE_ACCESS)).thenReturn(resourceAccess);

    Collection<GrantedAuthority> authorities = converter.convert(jwt);

    assertNotNull(authorities);
    assertTrue(authorities.isEmpty());
  }
}
