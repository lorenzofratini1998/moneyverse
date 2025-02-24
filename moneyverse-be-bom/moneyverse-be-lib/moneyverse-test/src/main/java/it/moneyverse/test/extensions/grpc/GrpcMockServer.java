package it.moneyverse.test.extensions.grpc;

import static org.grpcmock.GrpcMock.stubFor;
import static org.grpcmock.GrpcMock.unaryMethod;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.grpc.lib.*;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import org.grpcmock.junit5.GrpcMockExtension;

public class GrpcMockServer extends GrpcMockExtension {

  private final int port;
  private final String host = "localhost";

  public GrpcMockServer() {
    super(GrpcMockExtension.builder().withPort(0).build().getInstance());
    port = super.getPort();
  }

  public void mockExistentUser(UserModel user) {
    stubFor(
        unaryMethod(UserServiceGrpc.getGetUserByIdMethod())
            .willReturn(
                UserResponse.newBuilder()
                    .setUserId(user.getUserId().toString())
                    .setEmail(user.getEmail())
                    .setFirstName(user.getName())
                    .setLastName(user.getSurname())
                    .build()));
  }

  public void mockNonExistentUser() {
    stubFor(
        unaryMethod(UserServiceGrpc.getGetUserByIdMethod())
            .willReturn(UserResponse.getDefaultInstance()));
  }

  public void mockUserPreference(String currency) {
    stubFor(
        unaryMethod(UserServiceGrpc.getGetUserPreferenceMethod())
            .willReturn(
                UserPreferenceResponse.newBuilder()
                    .setPreferenceValue(
                        Math.random() < 0.5 ? currency : RandomUtils.randomString(3).toUpperCase())
                    .build()));
  }

  public void mockExistentAccount() {
    stubFor(
        unaryMethod(AccountServiceGrpc.getGetAccountByIdMethod())
            .willReturn(
                AccountResponse.newBuilder()
                    .setAccountId(RandomUtils.randomUUID().toString())
                    .setUserId(RandomUtils.randomUUID().toString())
                    .setAccountName(RandomUtils.randomString(30))
                    .setBalance(RandomUtils.randomBigDecimal().doubleValue())
                    .setBalanceTarget(RandomUtils.randomBigDecimal().doubleValue())
                    .setAccountCategory(RandomUtils.randomString(15))
                    .setAccountDescription(RandomUtils.randomString(30))
                    .setCurrency(RandomUtils.randomString(3).toUpperCase())
                    .build()));
  }

  public void mockNonExistentAccount() {
    stubFor(
        unaryMethod(AccountServiceGrpc.getGetAccountByIdMethod())
            .willReturn(AccountResponse.getDefaultInstance()));
  }

  public void mockExistentCategory() {
    stubFor(
        unaryMethod(BudgetServiceGrpc.getGetCategoryByIdMethod())
            .willReturn(
                CategoryResponse.newBuilder()
                    .setCategoryId(RandomUtils.randomUUID().toString())
                    .setUserId(RandomUtils.randomUUID().toString())
                    .setDescription(RandomUtils.randomString(30))
                    .setCategoryName(RandomUtils.randomString(30))
                    .build()));
  }

  public void mockNonExistentCategory() {
    stubFor(
        unaryMethod(BudgetServiceGrpc.getGetCategoryByIdMethod())
            .willReturn(CategoryResponse.getDefaultInstance()));
  }

  public void mockExistentCurrency(String isoCode) {
    stubFor(
        unaryMethod(CurrencyServiceGrpc.getGetCurrencyByCodeMethod())
            .willReturn(
                CurrencyResponse.newBuilder()
                    .setCurrencyId(RandomUtils.randomUUID().toString())
                    .setIsoCode(isoCode)
                    .build()));
  }

  public void mockExchangeRate(BigDecimal exchangeRate) {
    stubFor(
        unaryMethod(CurrencyServiceGrpc.getGetExchangeRateMethod())
            .willReturn(
                ExchangeRateResponse.newBuilder().setRate(exchangeRate.doubleValue()).build()));
  }

  public int getPort() {
    return port;
  }

  public String getHost() {
    return host;
  }
}
