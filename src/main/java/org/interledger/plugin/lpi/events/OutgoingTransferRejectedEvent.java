package org.interledger.plugin.lpi.events;

import org.interledger.ilp.InterledgerProtocolError;

import org.immutables.value.Value;

/**
 * Emitted after an outgoing transfer containing a condition has been rejected on the underlying
 * ledger (in other words, this connector prepared a transfer that was ultimately rejected by the
 * receiver on the underlying ledger).
 */
@Value.Immutable
public interface OutgoingTransferRejectedEvent extends OutgoingTransferEvent {

  InterledgerProtocolError getRejectionReason();
}
