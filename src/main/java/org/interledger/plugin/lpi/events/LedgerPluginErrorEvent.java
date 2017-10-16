package org.interledger.plugin.lpi.events;

import org.immutables.value.Value;

/**
 * Emitted after a ledger plugin disconnects from its underlying ledger.
 */
@Value.Immutable
public interface LedgerPluginErrorEvent extends LedgerPluginEvent {

  Exception getError();
}
