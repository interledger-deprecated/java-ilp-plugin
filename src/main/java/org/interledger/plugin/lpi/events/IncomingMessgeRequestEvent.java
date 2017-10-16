package org.interledger.plugin.lpi.events;

import org.interledger.InterledgerAddress;
import org.interledger.plugin.lpi.Message;

import org.immutables.value.Value;

/**
 * Emitted when an incoming request message arrives from another ledger participant.
 *
 * Hosts MUST NOT use these events to respond to requests. In order to provide responses, provide a
 * request handler via registerRequestHandler.
 */
@Value.Immutable
public interface IncomingMessgeRequestEvent extends IncomingLedgerPluginEvent {

  Message getMessage();

  /**
   * The ledger prefix of the ledger that emitted this event.
   */
  default InterledgerAddress getLedgerPrefix() {
    return getMessage().getLedgerPrefix();
  }
}
