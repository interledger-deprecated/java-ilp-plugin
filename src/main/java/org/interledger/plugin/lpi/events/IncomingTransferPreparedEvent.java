package org.interledger.plugin.lpi.events;

import org.interledger.plugin.lpi.Transfer;

import org.immutables.value.Value;

/**
 * Emitted after an incoming transfer containing a condition is prepared on the underlying ledger
 * (in other words, someone prepared a transfer that, when fulfilled, will be paid to the connector
 * running the plugin that emitted this event).
 *
 * Note that this event DOES NOT indicate that money has actually been transferred to the {@link
 * Transfer#getDestinationAccount()}.
 */
@Value.Immutable
public interface IncomingTransferPreparedEvent extends IncomingTransferEvent {

}
