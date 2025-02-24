package it.moneyverse.transaction.services;

import static it.moneyverse.transaction.utils.TransactionTestUtils.createTransferRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.moneyverse.core.model.dto.AccountDto;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.exceptions.AccountTransferException;
import it.moneyverse.transaction.model.dto.TransferDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.entities.Transfer;
import it.moneyverse.transaction.model.repositories.TransferRepository;
import it.moneyverse.transaction.runtime.messages.TransactionEventPublisher;
import it.moneyverse.transaction.utils.mapper.TransferMapper;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferManagementServiceTest {

  @InjectMocks private TransferManagementService transferManagementService;

  @Mock private AccountServiceClient accountServiceClient;
  @Mock private CurrencyServiceClient currencyServiceClient;
  @Mock private TransferRepository transferRepository;
  @Mock private TransactionEventPublisher eventPublisher;
  private MockedStatic<TransferMapper> transferMapper;

  @BeforeEach
  void setUp() {
    transferMapper = Mockito.mockStatic(TransferMapper.class);
  }

  @AfterEach
  void tearDown() {
    transferMapper.close();
  }

  @Test
  void givenTransferRequest_WhenCreateTransfer_ThenReturnTransfer(
      @Mock Transfer transfer, @Mock TransferDto transferDto, @Mock AccountDto accountDto) {
    UUID userId = RandomUtils.randomUUID();
    TransferRequestDto request = createTransferRequest(userId);

    Mockito.doNothing().when(currencyServiceClient).checkIfCurrencyExists(request.currency());
    when(accountServiceClient.getAccountById(request.fromAccount())).thenReturn(accountDto);
    when(accountServiceClient.getAccountById(request.toAccount())).thenReturn(accountDto);
    when(currencyServiceClient.convertCurrencyAmountByUserPreference(
            request.userId(), request.amount(), request.currency(), request.date()))
        .thenReturn(request.amount());
    when(currencyServiceClient.convertAmount(
            request.amount(), request.currency(), accountDto.getCurrency(), request.date()))
        .thenReturn(request.amount());
    when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);
    transferMapper.when(() -> TransferMapper.toTransferDto(transfer)).thenReturn(transferDto);

    TransferDto result = transferManagementService.createTransfer(request);

    assertNotNull(result);
    verify(transferRepository, times(1)).save(any(Transfer.class));
  }

  @Test
  void givenTransferRequest_WhenCreateTransfer_ThenAccountNotFound(
      @Mock TransferRequestDto request) {
    UUID accountId = RandomUtils.randomUUID();
    when(request.fromAccount()).thenReturn(accountId);
    when(request.toAccount()).thenReturn(accountId);

    assertThrows(
        AccountTransferException.class, () -> transferManagementService.createTransfer(request));

    verify(accountServiceClient, never()).getAccountById(any(UUID.class));
    verify(accountServiceClient, never()).getAccountById(any(UUID.class));
    verify(currencyServiceClient, never()).checkIfCurrencyExists(any(String.class));
    verify(transferRepository, never()).save(any(Transfer.class));
  }
}
