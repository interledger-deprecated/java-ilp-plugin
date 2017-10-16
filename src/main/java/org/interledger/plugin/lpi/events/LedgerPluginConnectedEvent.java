package org.interledger.plugin.lpi.events;

import org.interledger.InterledgerAddress;

import org.immutables.value.Value;

/**
 * Emitted after a ledger plugin connects to its underlying ledger.
 */
@Value.Immutable
public interface LedgerPluginConnectedEvent extends LedgerPluginEvent {

  InterledgerAddress getLedgerPrefix();
}
