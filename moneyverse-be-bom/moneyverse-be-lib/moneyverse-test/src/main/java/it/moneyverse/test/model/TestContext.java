package it.moneyverse.test.model;

import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.enums.UserRoleEnum;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.dto.UserCredential;
import it.moneyverse.test.model.entities.UserFactory;
import it.moneyverse.test.operations.keycloak.KeycloakTestSetupManager;
import it.moneyverse.test.utils.RandomUtils;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class TestContext<SELF extends TestContext<SELF>> {

  private final List<UserModel> users;
  private KeycloakTestSetupManager keycloakTestManager;

  protected TestContext() {
    users = UserFactory.createUsers();
  }

  public List<UserModel> getUsers() {
    return users;
  }

  public UserModel getRandomUser() {
    List<UserModel> users =
        getUsers().stream().filter(user -> !user.getRole().equals(UserRoleEnum.ADMIN)).toList();
    return users.get(RandomUtils.randomInteger(0, users.size() - 1));
  }

  public UserModel getAdminUser() {
    return getUsers().stream()
        .filter(user -> user.getRole().equals(UserRoleEnum.ADMIN))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No admin found"));
  }

  public String getAuthenticationToken(String username) {
    return keycloakTestManager.getTestAuthenticationToken(getUserCredential(username));
  }

  private UserCredential getUserCredential(String username) {
    return Optional.of(getUser(username))
        .map(user -> new UserCredential(user.getUsername(), user.getPassword()))
        .orElseThrow(() -> new IllegalArgumentException("No user found with username " + username));
  }

  private UserModel getUser(String username) {
    return getUsers().stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No user found with username " + username));
  }

  protected boolean filterByBound(BigDecimal value, BoundCriteria boundCriteria) {
    return boundCriteria.getLower().map(lower -> value.compareTo(lower) >= 0).orElse(true)
        && boundCriteria.getUpper().map(upper -> value.compareTo(upper) <= 0).orElse(true);
  }

  public <T> String createUri(String path, T criteria) {
    UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(path);
    for (Field field : criteria.getClass().getDeclaredFields()) {
      ReflectionUtils.makeAccessible(field);
      Object fieldValue = ReflectionUtils.getField(field, criteria);
      String fieldName = field.getName();

      if (fieldValue != null) {
        switch (fieldValue) {
          case BoundCriteria boundCriteria -> {
            boundCriteria
                .getLower()
                .ifPresent(lower -> uri.queryParam(fieldName + ".lower", lower));
            boundCriteria
                .getUpper()
                .ifPresent(upper -> uri.queryParam(fieldName + ".upper", upper));
          }
          case PageCriteria page -> {
            uri.queryParam(fieldName + ".offset", page.getOffset());
            uri.queryParam(fieldName + ".limit", page.getLimit());
          }
          case SortCriteria<? extends SortAttribute> sort -> {
            uri.queryParam(fieldName + ".attribute", sort.getAttribute());
            uri.queryParam(fieldName + ".direction", sort.getDirection());
          }
          default -> uri.queryParam(fieldName, fieldValue);
        }
      }
    }
    return uri.build().toUriString();
  }

  public abstract SELF self();

  public SELF insertUsersIntoKeycloak(KeycloakContainer keycloakContainer) {
    keycloakTestManager = new KeycloakTestSetupManager(keycloakContainer, users);
    keycloakTestManager.setup();
    return self();
  }

  public abstract SELF generateScript(Path dir);
}
