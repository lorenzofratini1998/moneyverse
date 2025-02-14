package it.moneyverse.transaction.services;

import static it.moneyverse.transaction.utils.TransactionTestUtils.createTransferRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.exceptions.AccountTransferException;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransferDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import it.moneyverse.transaction.model.repositories.TransferRepository;
import it.moneyverse.transaction.utils.mapper.TransferMapper;
import java.util.List;
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
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class TransferManagementServiceTest {

  @InjectMocks private TransferManagementService transferManagementService;

  @Mock private AccountServiceClient accountServiceClient;
  @Mock private CurrencyServiceClient currencyServiceClient;
  @Mock private TransferRepository transferRepository;
  @Mock private ApplicationEventPublisher eventPublisher;
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
      @Mock Transfer transfer, @Mock Transaction transaction, @Mock TransactionDto transactionDto) {
    UUID userId = RandomUtils.randomUUID();
    TransferRequestDto request = createTransferRequest(userId);

    Mockito.doNothing().when(accountServiceClient).checkIfAccountExists(request.fromAccount());
    Mockito.doNothing().when(accountServiceClient).checkIfAccountExists(request.toAccount());
    Mockito.doNothing().when(currencyServiceClient).checkIfCurrencyExists(request.currency());
    transferMapper.when(() -> TransferMapper.toTransfer(request)).thenReturn(transfer);
    when(transferRepository.save(transfer)).thenReturn(transfer);
    when(transfer.getTransactionFrom()).thenReturn(transaction);
    when(transfer.getTransactionTo()).thenReturn(transaction);
    transferMapper
        .when(() -> TransferMapper.toTransactionDto(transfer))
        .thenReturn(List.of(transactionDto));

    TransferDto result = transferManagementService.createTransfer(request);

    assertNotNull(result);
    verify(transferRepository, times(1)).save(transfer);
  }

  @Test
  void givenTransferRequest_WhenCreateTransfer_ThenAccountNotFound(
      @Mock TransferRequestDto request) {
    UUID accountId = RandomUtils.randomUUID();
    when(request.fromAccount()).thenReturn(accountId);
    when(request.toAccount()).thenReturn(accountId);

    assertThrows(
        AccountTransferException.class, () -> transferManagementService.createTransfer(request));

    verify(accountServiceClient, never()).checkIfAccountExists(any(UUID.class));
    verify(accountServiceClient, never()).checkIfAccountExists(any(UUID.class));
    verify(currencyServiceClient, never()).checkIfCurrencyExists(any(String.class));
    verify(transferRepository, never()).save(any(Transfer.class));
  }
}
