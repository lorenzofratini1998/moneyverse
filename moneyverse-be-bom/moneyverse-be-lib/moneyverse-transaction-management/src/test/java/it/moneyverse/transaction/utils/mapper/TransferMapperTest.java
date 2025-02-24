package it.moneyverse.transaction.utils.mapper;

import static it.moneyverse.transaction.utils.TransactionTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TransferUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Transfer;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TransferMapperTest {

  @Test
  void testPartialUpdate_NullTransfer() {
    assertNull(TransferMapper.partialUpdate(null, null));
  }

  @Test
  void testPartialUpdate_TransferUpdateRequest() {
    UUID userId = RandomUtils.randomUUID();
    Transfer transfer = createTransfer(userId);
    TransferUpdateRequestDto request = createTransferUpdateRequest();

    transfer = TransferMapper.partialUpdate(transfer, request);

    assertEquals(request.date(), transfer.getDate());
    assertEquals(request.date(), transfer.getTransactionFrom().getDate());
    assertEquals(request.date(), transfer.getTransactionTo().getDate());
    assertEquals(request.amount(), transfer.getAmount());
    assertEquals(
        request.amount().multiply(BigDecimal.valueOf(-1)),
        transfer.getTransactionFrom().getAmount());
    assertEquals(request.amount(), transfer.getTransactionTo().getAmount());
    assertEquals(request.currency(), transfer.getCurrency());
    assertEquals(request.currency(), transfer.getTransactionFrom().getCurrency());
    assertEquals(request.currency(), transfer.getTransactionTo().getCurrency());
    assertEquals(request.fromAccount(), transfer.getTransactionFrom().getAccountId());
    assertEquals(request.toAccount(), transfer.getTransactionTo().getAccountId());
  }
}
