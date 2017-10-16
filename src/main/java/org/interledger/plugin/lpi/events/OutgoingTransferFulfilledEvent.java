package org.interledger.plugin.lpi.events;

import org.interledger.cryptoconditions.Fulfillment;

import org.immutables.value.Value;

/**
 * Emitted after an outgoing transfer containing a condition is fulfilled on the underlying ledger
 * (in other words, a transfer that this connector prepared ).
 *
 * This event indicates that funds have been transferred. In order to prevent unexpected incoming
 * funds, a ledger MAY forbid accounts from fulfilling a transfer who are not the transfer's
 * receiver.
 */
@Value.Immutable
public interface OutgoingTransferFulfilledEvent extends OutgoingTransferEvent {

  Fulfillment getFulfillment();
}
