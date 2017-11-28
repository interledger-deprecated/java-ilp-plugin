package org.interledger.plugin.lpi;

import static org.mockito.Mockito.reset;

import org.interledger.plugin.lpi.MockLedgerPlugin.SimulatedLedger;
import org.interledger.plugin.lpi.events.IncomingMessgeRequestEvent;
import org.interledger.plugin.lpi.events.IncomingTransferCancelledEvent;
import org.interledger.plugin.lpi.events.IncomingTransferFulfilledEvent;
import org.interledger.plugin.lpi.events.IncomingTransferPreparedEvent;
import org.interledger.plugin.lpi.events.IncomingTransferRejectedEvent;
import org.interledger.plugin.lpi.events.LedgerInfoChangedEvent;
import org.interledger.plugin.lpi.events.LedgerPluginConnectedEvent;
import org.interledger.plugin.lpi.events.LedgerPluginDisconnectedEvent;
import org.interledger.plugin.lpi.events.LedgerPluginErrorEvent;
import org.interledger.plugin.lpi.events.OutgoingMessgeRequestEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferCancelledEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferFulfilledEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferPreparedEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferRejectedEvent;
import org.interledger.plugin.lpi.handlers.LedgerPluginEventHandler;

import ch.qos.logback.classic.Level;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;

import java.util.Objects;

import javax.money.Monetary;

/**
 * A unit test for {@link QueuedMockLedgerPlugin} using an EventBus to simulate async event
 * delivery.
 */
public class MockQueuedLedgerPluginTest extends AbstractMockLedgerPluginTest {

  // This simulates a queueing mechanism that can be used for queued event handling.
  private EventBus eventBus;

  @BeforeMethod
  public void setup() {
    MockitoAnnotations.initMocks(this);

    // Enable debug mode...
    ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
        .setLevel(Level.DEBUG);

    this.ledgerInfo = ImmutableLedgerInfo.builder()
        .currencyScale(8)
        .currencyUnit(Monetary.getCurrency("USD"))
        .ledgerPrefix(LEDGER_PREFIX)
        .build();
    final SimulatedLedger simulatedLedger = new SimulatedLedger(ledgerInfo);

    // Initialize the ledger plugin under test...
    this.eventBus = new EventBus();
    this.mockLedgerPlugin = new QueuedMockLedgerPlugin(getLedgerPluginConfig(), simulatedLedger,
        eventBus);

    // Wire-up an async event handler...
    final EventBusLedgerPluginEventHandler asyncLedgerPluginEventHandler
        = new EventBusLedgerPluginEventHandler(ledgerPluginEventHandlerMock);
    eventBus.register(asyncLedgerPluginEventHandler);
    mockLedgerPlugin.addLedgerPluginEventHandler(asyncLedgerPluginEventHandler);
    mockLedgerPlugin.connect();

    // Reset the event handler so we don't count the "connect" event, in general
    reset(ledgerPluginEventHandlerMock);
  }

  /**
   * Connects the event bus subscriber to a mock for unit testing purposes.
   */
  private static class EventBusLedgerPluginEventHandler implements LedgerPluginEventHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LedgerPluginEventHandler mockLedgerPluginEventHandler;

    private EventBusLedgerPluginEventHandler(
        final LedgerPluginEventHandler mockLedgerPluginEventHandler
    ) {
      this.mockLedgerPluginEventHandler = Objects.requireNonNull(mockLedgerPluginEventHandler);
    }

    @Override
    @Subscribe
    public void onTransferPrepared(IncomingTransferPreparedEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onTransferPrepared(event);
    }

    @Override
    @Subscribe
    public void onTransferFulfilled(IncomingTransferFulfilledEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onTransferFulfilled(event);
    }

    @Override
    @Subscribe
    public void onTransferCancelled(IncomingTransferCancelledEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onTransferCancelled(event);
    }

    @Override
    @Subscribe
    public void onTransferRejected(IncomingTransferRejectedEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onTransferRejected(event);
    }

    @Override
    @Subscribe
    public void onLedgerInfoChanged(LedgerInfoChangedEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onLedgerInfoChanged(event);
    }

    @Override
    @Subscribe
    public void onConnect(LedgerPluginConnectedEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onConnect(event);
    }

    @Override
    @Subscribe
    public void onDisconnect(LedgerPluginDisconnectedEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onDisconnect(event);
    }

    @Override
    @Subscribe
    public void onError(LedgerPluginErrorEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onError(event);
    }

    @Override
    @Subscribe
    public void onMessageRequest(IncomingMessgeRequestEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onMessageRequest(event);
    }

    @Override
    @Subscribe
    public void onMessageRequest(OutgoingMessgeRequestEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onMessageRequest(event);
    }

    @Override
    @Subscribe
    public void onTransferPrepared(OutgoingTransferPreparedEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onTransferPrepared(event);
    }

    @Override
    @Subscribe
    public void onTransferFulfilled(OutgoingTransferFulfilledEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onTransferFulfilled(event);
    }

    @Override
    @Subscribe
    public void onTransferCancelled(OutgoingTransferCancelledEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onTransferCancelled(event);
    }

    @Override
    @Subscribe
    public void onTransferRejected(OutgoingTransferRejectedEvent event) {
      logger.info("EventBus Subscriber: {}", event);
      mockLedgerPluginEventHandler.onTransferRejected(event);
    }

  }


}