package org.interledger.plugin.lpi.events;

import org.immutables.value.Value;
import org.interledger.plugin.lpi.Transfer;

/**
 * Emitted after an outgoing transfer containing a condition is prepared on the underlying ledger (in other words, a
 * ledger prepared by this connector).
 *
 * Note that this event DOES NOT indicate that money has actually been transferred to the {@link
 * Transfer#getDestinationAccount()}.
 */
@Value.Immutable
public interface OutgoingTransferPreparedEvent extends OutgoingTransferEvent {

}
