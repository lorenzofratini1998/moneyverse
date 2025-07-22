package it.moneyverse.analytics.model;

import it.moneyverse.analytics.model.entities.TransactionEvent;
import it.moneyverse.analytics.model.entities.TransactionEventBuffer;
import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.test.annotations.datasource.TestModelEntity;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import java.nio.file.Path;
import java.util.List;

public class AnalyticsTestContext extends TestContext<AnalyticsTestContext> {

  private static AnalyticsTestContext currentInstance;

  @TestModelEntity private final List<TransactionEvent> transactionEvents;
  @TestModelEntity private final List<TransactionEventBuffer> transactionEventBuffers;

  public AnalyticsTestContext(KeycloakContainer keycloakContainer) {
    super(keycloakContainer);
    transactionEvents = TransactionEventTestFactory.createTransactionEvents(getUsers());
    transactionEventBuffers = createTransactionEventsBuffer(transactionEvents);
    setCurrentInstance(this);
  }

  public AnalyticsTestContext() {
    super();
    transactionEvents =
        TransactionEventTestFactory.createTransactionEvents(getUsers()).stream()
            .limit(1000)
            .toList();
    transactionEventBuffers = createTransactionEventsBuffer(transactionEvents);
    setCurrentInstance(this);
  }

  private List<TransactionEventBuffer> createTransactionEventsBuffer(
      List<TransactionEvent> transactionEvents) {
    return transactionEvents.stream()
        .map(
            t -> {
              TransactionEventBuffer transactionEventBuffer = new TransactionEventBuffer();
              transactionEventBuffer.setEventId(t.getEventId());
              transactionEventBuffer.setEventType(EventTypeEnum.fromInteger(t.getEventType()));
              transactionEventBuffer.setUserId(t.getUserId());
              transactionEventBuffer.setTransactionId(t.getTransactionId());
              transactionEventBuffer.setOriginalTransactionId(t.getOriginalTransactionId());
              transactionEventBuffer.setAccountId(t.getAccountId());
              transactionEventBuffer.setCategoryId(t.getCategoryId());
              transactionEventBuffer.setBudgetId(t.getBudgetId());
              transactionEventBuffer.setAmount(t.getAmount());
              transactionEventBuffer.setNormalizedAmount(t.getNormalizedAmount());
              transactionEventBuffer.setCurrency(t.getCurrency());
              transactionEventBuffer.setDate(t.getDate());
              transactionEventBuffer.setEventTimestamp(t.getEventTimestamp());
              return transactionEventBuffer;
            })
        .toList();
  }

  @Override
  public AnalyticsTestContext self() {
    return this;
  }

  @Override
  public AnalyticsTestContext generateScript(Path dir) {
    EntityScriptGenerator scriptGenerator =
        new EntityScriptGenerator(
            new ScriptMetadata(dir, transactionEvents), new SQLScriptService());
    StringBuilder script = scriptGenerator.generateScript();
    scriptGenerator.save(script);
    return self();
  }

  public AnalyticsTestContext generateScriptBatch(Path dir) {
    EntityScriptGenerator scriptGenerator =
        new EntityScriptGenerator(
            new ScriptMetadata(dir, transactionEventBuffers), new SQLScriptService());
    StringBuilder script = scriptGenerator.generateScript();
    scriptGenerator.save(script);
    return self();
  }

  private static void setCurrentInstance(AnalyticsTestContext testContext) {
    currentInstance = testContext;
  }

  public static AnalyticsTestContext getCurrentInstance() {
    if (currentInstance == null) {
      throw new IllegalStateException("TestContext instance is not set.");
    }
    return currentInstance;
  }

  public List<TransactionEvent> getTransactionEvents() {
    return transactionEvents;
  }
}
