package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.exceptions.ResourceStillExistsException;
import it.moneyverse.core.model.dto.AccountDto;
import it.moneyverse.test.utils.RandomUtils;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceGrpcClientTest {

  @InjectMocks AccountServiceGrpcClient accountServiceGrpcClient;
  @Mock AccountGrpcService accountGrpcService;

  @Test
  void testGetByAccountId(@Mock AccountDto accountDto) {
    UUID accountId = RandomUtils.randomUUID();
    when(accountGrpcService.getAccountById(accountId)).thenReturn(Optional.of(accountDto));

    Optional<AccountDto> response = accountServiceGrpcClient.getAccountById(accountId);

    assertNotNull(response);
    assertTrue(response.isPresent());
    verify(accountGrpcService, times(1)).getAccountById(accountId);
  }

  @Test
  void testCheckIfAccountExists(@Mock AccountDto accountDto) {
    UUID accountId = RandomUtils.randomUUID();
    when(accountGrpcService.getAccountById(accountId)).thenReturn(Optional.of(accountDto));

    assertDoesNotThrow(() -> accountServiceGrpcClient.checkIfAccountExists(accountId));

    verify(accountGrpcService, times(1)).getAccountById(accountId);
  }

  @Test
  void testCheckIfAccountExists_Exception() {
    UUID accountId = RandomUtils.randomUUID();
    when(accountGrpcService.getAccountById(accountId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> accountServiceGrpcClient.checkIfAccountExists(accountId));
    verify(accountGrpcService, times(1)).getAccountById(accountId);
  }

  @Test
  void testCheckIfAccountStillExists() {
    UUID accountId = RandomUtils.randomUUID();
    when(accountGrpcService.getAccountById(accountId)).thenReturn(Optional.empty());

    assertDoesNotThrow(() -> accountServiceGrpcClient.checkIfAccountStillExists(accountId));

    verify(accountGrpcService, times(1)).getAccountById(accountId);
  }

  @Test
  void testCheckIfAccountStillExists_Exception(@Mock AccountDto accountDto) {
    UUID accountId = RandomUtils.randomUUID();
    when(accountGrpcService.getAccountById(accountId)).thenReturn(Optional.of(accountDto));

    assertThrows(
        ResourceStillExistsException.class,
        () -> accountServiceGrpcClient.checkIfAccountStillExists(accountId));
    verify(accountGrpcService, times(1)).getAccountById(accountId);
  }
}
