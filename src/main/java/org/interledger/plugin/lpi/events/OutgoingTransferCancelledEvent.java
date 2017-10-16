package org.interledger.plugin.lpi.events;

import org.interledger.ilp.InterledgerProtocolError;

import org.immutables.value.Value;

/**
 * Emitted after an outgoing transfer is cancelled  by the receiver. This will happen on a timeout,
 * triggered by the ledger and not by the receiver, for a outgoing transfer that was created by this
 * connector.
 */
@Value.Immutable
public interface OutgoingTransferCancelledEvent extends OutgoingTransferEvent {

  InterledgerProtocolError getCancellationReason();
}
