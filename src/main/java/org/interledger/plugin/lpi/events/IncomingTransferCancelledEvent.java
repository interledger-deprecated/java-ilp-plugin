package org.interledger.plugin.lpi.events;

import org.interledger.ilp.InterledgerProtocolError;

import org.immutables.value.Value;

/**
 * Emitted after an incoming transfer is rejected by the underlying ledger. This will happen on a
 * timeout, triggered by the ledger and/or an atomic validator, and not by the receiver.
 */
@Value.Immutable
public interface IncomingTransferCancelledEvent extends IncomingTransferEvent {

  InterledgerProtocolError getCancellationReason();
}
