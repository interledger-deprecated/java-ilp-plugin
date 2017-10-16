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

/**
 * Defines how to emit events to a ledger plugin. A given {@link LedgerPlugin} has only a single
 * event-emitter, so depending on the implementation, it might be necessary to filter events before
 * actually sending to a particular ledger plugin.
 */
public interface LedgerPluginEventEmitter {

  void emitEvent(final LedgerPluginConnectedEvent event);

  void emitEvent(final LedgerPluginDisconnectedEvent event);

  void emitEvent(final LedgerPluginErrorEvent event);

  void emitEvent(final OutgoingTransferPreparedEvent event);

  void emitEvent(final OutgoingTransferFulfilledEvent event);

  void emitEvent(final OutgoingTransferRejectedEvent event);

  void emitEvent(final OutgoingTransferCancelledEvent event);

  void emitEvent(final OutgoingMessgeRequestEvent event);

  void emitEvent(final IncomingTransferPreparedEvent event);

  void emitEvent(final IncomingTransferFulfilledEvent event);

  void emitEvent(final IncomingTransferRejectedEvent event);

  void emitEvent(final IncomingTransferCancelledEvent event);

  void emitEvent(final IncomingMessgeRequestEvent event);
}
