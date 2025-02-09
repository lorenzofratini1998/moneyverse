package it.moneyverse.transaction.utils.mapper;

import static it.moneyverse.transaction.utils.TransactionTestUtils.createTransferRequest;
import static org.junit.jupiter.api.Assertions.*;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.entities.Transfer;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TransferMapperTest {

  @Test
  void testToTransfer_NullTransferRequest() {
    assertNull(TransferMapper.toTransfer(null));
  }

  @Test
  void testToTransfer_TransferRequest() {
    UUID userId = RandomUtils.randomUUID();
    TransferRequestDto request = createTransferRequest(userId);

    Transfer transfer = TransferMapper.toTransfer(request);

    assertNotNull(transfer);
    assertEquals(request.date(), transfer.getDate());
    assertEquals(request.amount(), transfer.getAmount());
    assertEquals(request.currency(), transfer.getCurrency());

    assertNotNull(transfer.getTransactionFrom());
    assertEquals(userId, transfer.getTransactionFrom().getUserId());
    assertEquals(request.fromAccount(), transfer.getTransactionFrom().getAccountId());

    assertNotNull(transfer.getTransactionTo());
    assertEquals(userId, transfer.getTransactionTo().getUserId());
    assertEquals(request.toAccount(), transfer.getTransactionTo().getAccountId());
  }
}
