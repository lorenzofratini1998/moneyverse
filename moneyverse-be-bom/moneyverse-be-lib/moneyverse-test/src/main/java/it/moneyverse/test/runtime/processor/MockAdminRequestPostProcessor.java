package it.moneyverse.test.runtime.processor;

import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.auth.AuthenticatedUser;
import it.moneyverse.test.utils.RandomUtils;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class MockAdminRequestPostProcessor {

  public static RequestPostProcessor mockAdmin() {
    return request -> {
      SecurityMockMvcRequestPostProcessors.jwt().postProcessRequest(request);
      SecurityMockMvcRequestPostProcessors.authentication(createMockAuthentication())
          .postProcessRequest(request);
      return request;
    };
  }

  private static Authentication createMockAuthentication() {
    Collection<GrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority("ROLE_" + UserRoleEnum.ADMIN));
    Principal principal =
        AuthenticatedUser.builder()
            .withUsername(RandomUtils.randomString(15))
            .withAuthorities(authorities)
            .build();
    return new UsernamePasswordAuthenticationToken(principal, null, authorities);
  }
}
