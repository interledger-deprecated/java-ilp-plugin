package org.interledger.plugin.lpi;

import org.interledger.plugin.lpi.events.IncomingMessgeRequestEvent;
import org.interledger.plugin.lpi.events.IncomingTransferCancelledEvent;
import org.interledger.plugin.lpi.events.IncomingTransferFulfilledEvent;
import org.interledger.plugin.lpi.events.IncomingTransferPreparedEvent;
import org.interledger.plugin.lpi.events.IncomingTransferRejectedEvent;
import org.interledger.plugin.lpi.events.LedgerPluginConnectedEvent;
import org.interledger.plugin.lpi.events.LedgerPluginDisconnectedEvent;
import org.interledger.plugin.lpi.events.LedgerPluginErrorEvent;
import org.interledger.plugin.lpi.events.OutgoingMessgeRequestEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferCancelledEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferFulfilledEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferPreparedEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferRejectedEvent;

import com.google.common.eventbus.EventBus;

import java.util.Objects;

/**
 * A demonstration implementation of {@link LedgerPlugin} that simulates an underlying ledger while
 * handling events from the underlying ledger in an asynchronous manner.
 *
 * This implementation uses a Guava {@link EventBus} as its queuing mechanism to illustrate
 * interface boundaries. However, an actual implementation would probably use some sort of queuing
 * system, for example JMS.
 *
 * The following is a high-level component and flow diagram for events emitted from the underlying
 * ledger into this ledger plugin:
 *
 * <pre>
 *                      Ledger
 *                      Events───────┐
 *                      │            │
 *                      │            ▼
 *        ┌──────────┐  │  ┌────────────────┐
 *     ┌─▶│EventQueue│──┘  │  LedgerPlugin  │
 *     │  └──────────┘     │ Event Emitter  │
 *     │                   └────────────────┘
 *     │                            │
 *     │             ┌──────onEvent─┘
 *     │             │
 *     │             ▼
 *     │     ┌──────────────┬──────────────┐
 *     │     │ LedgerPlugin │              │
 *     │     │Event Handlers│ LedgerPlugin │
 *     │     │              │              │
 *     │     └──────────────┴──────────────┘
 *     │                            │
 *     │                            ▼
 *     │                     ┌─────────────┐
 *     │                     │  Simulated  │
 * Ledger Events─────────────│   Ledger    │
 *                           └─────────────┘
 * </pre>
 */
public class QueuedMockLedgerPlugin extends MockLedgerPlugin implements LedgerPlugin {

  /**
   * Required-args Constructor.
   *
   * @param ledgerPluginConfig A {@link ExtendedLedgerPluginConfig} of configuration strings for
   *                           this plugin.
   */
  protected QueuedMockLedgerPlugin(
      final ExtendedLedgerPluginConfig ledgerPluginConfig,
      final SimulatedLedger simulatedLedger,
      final EventBus eventBus
  ) {
    super(
        ledgerPluginConfig, simulatedLedger, new AsyncLedgerPluginEventEmitter(eventBus)
    );
  }

  /**
   * An example {@link LedgerPluginEventEmitter} that allows events to be asynchronously emitted
   * into a {@link LedgerPlugin} using an {@link EventBus} as a simulated queueing mechanism.
   *
   * For an synchronous example, consider {@link SyncLedgerPluginEventEmitter} instead.
   */
  public static class AsyncLedgerPluginEventEmitter implements LedgerPluginEventEmitter {

    private final EventBus eventBus;

    private AsyncLedgerPluginEventEmitter(final EventBus eventBus) {
      this.eventBus = Objects.requireNonNull(eventBus);
    }

    @Override
    public void emitEvent(LedgerPluginConnectedEvent event) {
      eventBus.post(event);
    }

    @Override
    public void emitEvent(LedgerPluginDisconnectedEvent event) {
      eventBus.post(event);
    }

    @Override
    public void emitEvent(LedgerPluginErrorEvent event) {
      eventBus.post(event);
    }

    @Override
    public void emitEvent(OutgoingTransferPreparedEvent event) {
      eventBus.post(event);
    }

    @Override
    public void emitEvent(OutgoingTransferFulfilledEvent event) {
      eventBus.post(event);
    }

    @Override
    public void emitEvent(OutgoingTransferRejectedEvent event) {
      eventBus.post(event);
    }

    @Override
    public void emitEvent(OutgoingTransferCancelledEvent event) {
      eventBus.post(event);
    }

    @Override
    public void emitEvent(OutgoingMessgeRequestEvent event) {
      eventBus.post(event);
    }

    @Override
    public void emitEvent(IncomingTransferPreparedEvent event) {
      eventBus.post(event);
    }

    @Override
    public void emitEvent(IncomingTransferFulfilledEvent event) {
      eventBus.post(event);
    }

    @Override
    public void emitEvent(IncomingTransferRejectedEvent event) {
      eventBus.post(event);
    }

    @Override
    public void emitEvent(IncomingTransferCancelledEvent event) {
      eventBus.post(event);
    }

    @Override
    public void emitEvent(IncomingMessgeRequestEvent event) {
      eventBus.post(event);
    }
  }

}
