package it.moneyverse.transaction.utils.mapper;

import static org.junit.jupiter.api.Assertions.*;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.TransferTestFactory;
import it.moneyverse.transaction.model.dto.TransferUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Transfer;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TransferMapperTest {

  private static final UUID USER_ID = RandomUtils.randomUUID();

  @Test
  void testPartialUpdate_NullTransfer() {
    assertNull(TransferMapper.partialUpdate(null, null));
  }

  @Test
  void testPartialUpdate_TransferUpdateRequest() {
    Transfer transfer = TransferTestFactory.fakeTransfer(USER_ID);
    TransferUpdateRequestDto request =
        TransferTestFactory.TransferUpdateRequestBuilder.defaultInstance();

    transfer = TransferMapper.partialUpdate(transfer, request);

    assertEquals(request.date(), transfer.getDate());
    assertEquals(request.amount(), transfer.getAmount());
    assertEquals(request.currency(), transfer.getCurrency());
    assertEquals(request.date(), transfer.getTransactionFrom().getDate());
    assertEquals(request.amount().negate(), transfer.getTransactionFrom().getAmount());
    assertEquals(request.currency(), transfer.getTransactionFrom().getCurrency());
    assertEquals(request.fromAccount(), transfer.getTransactionFrom().getAccountId());
    assertEquals(request.date(), transfer.getTransactionTo().getDate());
    assertEquals(request.amount(), transfer.getTransactionTo().getAmount());
    assertEquals(request.currency(), transfer.getTransactionTo().getCurrency());
    assertEquals(request.toAccount(), transfer.getTransactionTo().getAccountId());
  }
}
