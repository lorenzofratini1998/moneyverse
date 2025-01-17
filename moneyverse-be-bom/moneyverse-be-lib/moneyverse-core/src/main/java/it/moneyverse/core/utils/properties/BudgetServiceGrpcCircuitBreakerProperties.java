package it.moneyverse.core.utils.properties;

import static it.moneyverse.core.utils.properties.BudgetServiceGrpcClientProperties.BUDGET_SERVICE_CLIENT_PREFIX;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = BudgetServiceGrpcCircuitBreakerProperties.PREFIX)
public class BudgetServiceGrpcCircuitBreakerProperties {

  public static final String BUDGET_SERVICE_GRPC = "budgetServiceGrpc";
  public static final String PREFIX = BUDGET_SERVICE_CLIENT_PREFIX + ".circuit-breaker";
  public static final String FAILURE_RATE_THRESHOLD = PREFIX + ".failure-rate-threshold";
  public static final String WAIT_DURATION_IN_OPEN_STATE = PREFIX + ".wait-duration-in-open-state";
  public static final String SLIDING_WINDOW_SIZE = PREFIX + ".sliding-window-size";
  private static final Integer FAILURE_RATE_THRESHOLD_DEFAULT = 50;
  private static final Integer WAIT_DURATION_IN_OPEN_STATE_DEFAULT = 10;
  private static final Integer SLIDING_WINDOW_SIZE_DEFAULT = 10;

  private final Integer failureRateThreshold;
  private final Integer waitDurationInOpenState;
  private final Integer slidingWindowSize;

  @ConstructorBinding
  public BudgetServiceGrpcCircuitBreakerProperties(
      Integer failureRateThreshold, Integer waitDurationInOpenState, Integer slidingWindowSize) {
    this.failureRateThreshold =
        failureRateThreshold != null ? failureRateThreshold : FAILURE_RATE_THRESHOLD_DEFAULT;
    this.waitDurationInOpenState =
        waitDurationInOpenState != null
            ? waitDurationInOpenState
            : WAIT_DURATION_IN_OPEN_STATE_DEFAULT;
    this.slidingWindowSize =
        slidingWindowSize != null ? slidingWindowSize : SLIDING_WINDOW_SIZE_DEFAULT;
  }

  public Integer getFailureRateThreshold() {
    return failureRateThreshold;
  }

  public Integer getWaitDurationInOpenState() {
    return waitDurationInOpenState;
  }

  public Integer getSlidingWindowSize() {
    return slidingWindowSize;
  }
}
