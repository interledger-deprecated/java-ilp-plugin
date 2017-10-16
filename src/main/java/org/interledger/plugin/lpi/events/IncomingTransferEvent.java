package org.interledger.plugin.lpi.events;

import org.interledger.InterledgerAddress;
import org.interledger.plugin.lpi.Transfer;

/**
 * A parent interface for incoming events related to ledger transfers.
 */
public interface IncomingTransferEvent extends IncomingLedgerPluginEvent {

  /**
   * A transfer that was prepared for the connector running a ledger plugin.
   */
  Transfer getTransfer();

  /**
   * The ledger prefix of the ledger that emitted this event.
   */
  default InterledgerAddress getLedgerPrefix() {
    return getTransfer().getLedgerPrefix();
  }
}
