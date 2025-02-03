package it.moneyverse.test.runtime.processor;

import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.auth.AuthenticatedUser;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class MockUserRequestPostProcessor {

  public static RequestPostProcessor mockUser(String username) {
    return request -> {
      SecurityMockMvcRequestPostProcessors.jwt().postProcessRequest(request);
      SecurityMockMvcRequestPostProcessors.authentication(createMockAuthentication(username))
          .postProcessRequest(request);
      return request;
    };
  }

  public static RequestPostProcessor mockUser(UUID userId) {
    return request -> {
      SecurityMockMvcRequestPostProcessors.jwt().postProcessRequest(request);
      SecurityMockMvcRequestPostProcessors.authentication(createMockAuthentication(userId))
          .postProcessRequest(request);
      return request;
    };
  }

  private static Authentication createMockAuthentication(String username) {
    Collection<GrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority("ROLE_" + UserRoleEnum.USER));
    Principal principal =
        AuthenticatedUser.builder().withUsername(username).withAuthorities(authorities).build();
    return new UsernamePasswordAuthenticationToken(principal, null, authorities);
  }

  private static Authentication createMockAuthentication(UUID userId) {
    Collection<GrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority("ROLE_" + UserRoleEnum.USER));
    Principal principal =
        AuthenticatedUser.builder().withId(userId).withAuthorities(authorities).build();
    return new UsernamePasswordAuthenticationToken(principal, null, authorities);
  }
}
