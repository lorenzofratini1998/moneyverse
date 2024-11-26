package it.moneyverse.account;

import it.moneyverse.annotations.MoneyverseApplication;
import org.springframework.boot.SpringApplication;

@MoneyverseApplication
public class AccountManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccountManagementApplication.class, args);
  }
}