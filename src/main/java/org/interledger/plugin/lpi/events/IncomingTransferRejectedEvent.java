package org.interledger.plugin.lpi.events;

import org.interledger.ilp.InterledgerProtocolError;

import org.immutables.value.Value;

/**
 * Emitted after an incoming transfer is rejected by the receiver (which is the connector operating
 * this ledger plugin). This indicates that a transfer has been manually cancelled before the
 * timeout by the receiver. A message can be passed along with the rejection.
 */
@Value.Immutable
public interface IncomingTransferRejectedEvent extends IncomingTransferEvent {

  InterledgerProtocolError getRejectionReason();
}
