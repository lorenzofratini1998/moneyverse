package it.moneyverse.analytics.runtime.batch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.moneyverse.analytics.model.entities.TransactionEventBuffer;
import it.moneyverse.analytics.model.repositories.TransactionEventBufferRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class TransactionEventReaderTest {
  @Mock TransactionEventBufferRepository transactionEventBufferRepository;
  @InjectMocks TransactionEventReader transactionEventReader;

  @Test
  void testRead(@Mock TransactionEventBuffer transactionEventBuffer) {
    Page<TransactionEventBuffer> mockPage =
        new PageImpl<>(Collections.singletonList(transactionEventBuffer));

    when(transactionEventBufferRepository.findAll(any(PageRequest.class))).thenReturn(mockPage);

    List<TransactionEventBuffer> result = transactionEventReader.read();

    assertNotNull(result);
    assertFalse(result.isEmpty());
    verify(transactionEventBufferRepository, times(1)).findAll(any(PageRequest.class));
  }
}
