package org.interledger.plugin.lpi.events;

import org.interledger.plugin.lpi.Message;

import org.immutables.value.Value;

/**
 * Emitted when a response message is received from an underlying ledger.
 *
 * @deprecated While technically a part of IL-RFC-4, this event will likely be removed in favor of a
 *     synchronous response model that allows an immediately acknowledgement to be returned from a
 *     send-message transmission, and then a new incoming request to represent a correlated
 *     response.
 */
@Value.Immutable
@Deprecated
public interface IncomingMessgeResponseEvent extends IncomingLedgerPluginEvent {

  Message getMessage();
}
