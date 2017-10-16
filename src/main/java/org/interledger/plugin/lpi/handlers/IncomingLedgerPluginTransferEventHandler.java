package org.interledger.plugin.lpi.handlers;

import org.interledger.plugin.lpi.events.IncomingTransferCancelledEvent;
import org.interledger.plugin.lpi.events.IncomingTransferFulfilledEvent;
import org.interledger.plugin.lpi.events.IncomingTransferPreparedEvent;
import org.interledger.plugin.lpi.events.IncomingTransferRejectedEvent;

/**
 * An event handler for "incoming" transfer events relating to transfers that have been prepared for
 * the connector account operating the ledger plugin from some other source account on the same
 * ledger.
 */
public interface IncomingLedgerPluginTransferEventHandler {

  /**
   * Called to handle an {@link IncomingTransferPreparedEvent}.
   *
   * @param event A {@link IncomingTransferPreparedEvent}.
   */
  void onTransferPrepared(IncomingTransferPreparedEvent event);

  /**
   * Called to handle an {@link IncomingTransferFulfilledEvent}.
   *
   * @param event A {@link IncomingTransferFulfilledEvent}.
   */
  void onTransferFulfilled(IncomingTransferFulfilledEvent event);

  /**
   * Called to handle an {@link IncomingTransferCancelledEvent}.
   *
   * @param event A {@link IncomingTransferCancelledEvent}.
   */
  void onTransferCancelled(IncomingTransferCancelledEvent event);

  /**
   * Called to handle an {@link IncomingTransferRejectedEvent}.
   *
   * @param event A {@link IncomingTransferRejectedEvent}.
   */
  void onTransferRejected(IncomingTransferRejectedEvent event);

}
