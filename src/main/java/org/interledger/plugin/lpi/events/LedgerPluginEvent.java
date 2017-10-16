package org.interledger.plugin.lpi.events;

import org.interledger.InterledgerAddress;

/**
 * A parent interface for all ledger plugin events.
 */
public interface LedgerPluginEvent {

  /**
   * The ledger prefix of the ledger that emitted this event.
   */
  InterledgerAddress getLedgerPrefix();
}
