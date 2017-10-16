package org.interledger.plugin.lpi.events;

import org.interledger.InterledgerAddress;
import org.interledger.plugin.lpi.Transfer;

/**
 * A parent interface for outgoing events related to ledger transfers.
 */
public interface OutgoingTransferEvent extends OutgoingLedgerPluginEvent {

  /**
   * An identifier for a local-ledger transfer that was prepared by this connector for a destination
   * account on a given ledger plugin.
   */
  Transfer getTransfer();

  /**
   * The ledger prefix of the ledger that emitted this event.
   */
  default InterledgerAddress getLedgerPrefix() {
    return getTransfer().getLedgerPrefix();
  }
}
