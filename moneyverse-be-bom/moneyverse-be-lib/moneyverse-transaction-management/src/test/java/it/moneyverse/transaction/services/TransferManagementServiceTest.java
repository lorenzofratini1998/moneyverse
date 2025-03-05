package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.transaction.model.TransferTestFactory;
import it.moneyverse.transaction.model.dto.TransferDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import it.moneyverse.transaction.model.factories.TransferFactory;
import it.moneyverse.transaction.model.repositories.TransferRepository;
import it.moneyverse.transaction.model.validator.TransactionValidator;
import it.moneyverse.transaction.runtime.messages.TransactionEventPublisher;
import it.moneyverse.transaction.utils.mapper.TransferMapper;
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
  @Mock private TransactionValidator transactionValidator;
  private MockedStatic<TransferMapper> transferMapper;
  private MockedStatic<TransferFactory> transferFactory;

  @BeforeEach
  void setUp() {
    transferMapper = Mockito.mockStatic(TransferMapper.class);
    transferFactory = Mockito.mockStatic(TransferFactory.class);
  }

  @AfterEach
  void tearDown() {
    transferMapper.close();
    transferFactory.close();
  }

  @Test
  void givenTransferRequest_WhenCreateTransfer_ThenReturnTransfer(
      @Mock Transfer transfer, @Mock TransferDto transferDto) {
    TransferRequestDto request = TransferTestFactory.TransferRequestBuilder.defaultInstance();

    Mockito.doNothing().when(transactionValidator).validate(request);
    when(currencyServiceClient.convertCurrencyAmountByUserPreference(
            request.userId(), request.amount(), request.currency(), request.date()))
        .thenReturn(request.amount());
    transferFactory
        .when(
            () ->
                TransferFactory.createTransfer(
                    eq(request), any(Transaction.class), any(Transaction.class)))
        .thenReturn(transfer);
    when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);
    transferMapper.when(() -> TransferMapper.toTransferDto(transfer)).thenReturn(transferDto);

    TransferDto result = transferManagementService.createTransfer(request);

    assertNotNull(result);
    verify(transferRepository, times(1)).save(any(Transfer.class));
  }
}
