package it.moneyverse.core.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.auth.AuthenticatedUser;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class SecurityContextUtilsTest {

  private MockedStatic<SecurityContextHolder> securityContextHolderMockedStatic;
  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;
  @Mock private AuthenticatedUser authenticatedUser;
  @Mock private GrantedAuthority grantedAuthority;

  @BeforeEach
  void setUp() {
    securityContextHolderMockedStatic = mockStatic(SecurityContextHolder.class);
    securityContextHolderMockedStatic
        .when(SecurityContextHolder::getContext)
        .thenReturn(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(authenticatedUser);
  }

  @AfterEach
  void tearDown() {
    securityContextHolderMockedStatic.close();
  }

  @Test
  void testGetAuthenticatedUser() {
    when(authenticatedUser.getUsername()).thenReturn("testUser");

    AuthenticatedUser result = SecurityContextUtils.getAuthenticatedUser();

    assertNotNull(result);
    assertEquals("testUser", result.getUsername());
  }

  @Test
  void testIsCurrentUserAdmin() {
    when(authenticatedUser.getAuthorities()).thenReturn(List.of(grantedAuthority));
    when(grantedAuthority.getAuthority()).thenReturn("ROLE_" + UserRoleEnum.ADMIN.name());

    Boolean result = SecurityContextUtils.isCurrentUserAdmin();

    assertTrue(result);
  }

  @Test
  void testIsCurrentUserNotAdmin() {
    when(authenticatedUser.getAuthorities()).thenReturn(List.of(grantedAuthority));
    when(grantedAuthority.getAuthority()).thenReturn("ROLE_" + UserRoleEnum.USER.name());

    Boolean result = SecurityContextUtils.isCurrentUserAdmin();

    assertFalse(result);
  }
}
