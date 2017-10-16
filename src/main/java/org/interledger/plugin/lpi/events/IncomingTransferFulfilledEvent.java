package org.interledger.plugin.lpi.events;

import org.interledger.cryptoconditions.Fulfillment;

import org.immutables.value.Value;

/**
 * Emitted after an incoming transfer containing a condition is fulfilled on the underlying ledger
 * (in other words, someone prepared a transfer that was fulfilled, and so the connector running the
 * plugin that emitted this event was paid on the underlying ledger).
 *
 * This event indicates that funds have been transferred. In order to prevent unexpected incoming
 * funds, a ledger MAY forbid accounts from fulfilling a transfer who are not the transfer's
 * receiver.
 */
@Value.Immutable
public interface IncomingTransferFulfilledEvent extends IncomingTransferEvent {

  Fulfillment getFulfillment();
}
