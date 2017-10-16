package org.interledger.plugin.lpi.events;

import org.interledger.InterledgerAddress;
import org.interledger.plugin.lpi.Message;

import org.immutables.value.Value;

/**
 * Emitted when an outgoing request message is sent (outgoing_request) by/from a ledger plugin.
 */
@Value.Immutable
public interface OutgoingMessgeRequestEvent extends OutgoingLedgerPluginEvent {

  Message getMessage();

  /**
   * The ledger prefix of the ledger that emitted this event.
   */
  default InterledgerAddress getLedgerPrefix() {
    return getMessage().getLedgerPrefix();
  }
}
