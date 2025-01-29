package it.moneyverse.test.extensions.grpc;

import static org.grpcmock.GrpcMock.stubFor;
import static org.grpcmock.GrpcMock.unaryMethod;

import it.moneyverse.grpc.lib.*;
import org.grpcmock.junit5.GrpcMockExtension;

public class GrpcMockServer extends GrpcMockExtension {

  private final int port;
  private final String host = "localhost";

  public GrpcMockServer() {
    super(GrpcMockExtension.builder().withPort(0).build().getInstance());
    port = super.getPort();
  }

  public void mockExistentUser() {
    stubFor(
        unaryMethod(UserServiceGrpc.getCheckIfUserExistsMethod())
            .willReturn(UserResponse.newBuilder().setExists(true).build()));
  }

  public void mockExistentAccount() {
    stubFor(
        unaryMethod(AccountServiceGrpc.getCheckIfAccountExistsMethod())
            .willReturn(AccountResponse.newBuilder().setExists(true).build()));
  }

  public void mockExistentBudget() {
    stubFor(
        unaryMethod(BudgetServiceGrpc.getCheckIfBudgetExistsMethod())
            .willReturn(BudgetResponse.newBuilder().setExists(true).build()));
  }

  public void mockExistentCurrency() {
    stubFor(
        unaryMethod(CurrencyServiceGrpc.getCheckIfCurrencyExistsMethod())
            .willReturn(CurrencyResponse.newBuilder().setExists(true).build()));
  }

  public int getPort() {
    return port;
  }

  public String getHost() {
    return host;
  }
}
