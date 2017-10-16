package org.interledger.plugin.lpi.handlers;

import org.interledger.plugin.lpi.events.OutgoingTransferCancelledEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferFulfilledEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferPreparedEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferRejectedEvent;

/**
 * An event handler for "outgoing" transfer events, meaning transfers that have been prepared by
 * (and coming from) the connector account operating the ledger plugin to some other destination
 * account on the same ledger.
 */
public interface OutgoingLedgerPluginTransferEventHandler {

  /**
   * Called to handle an {@link OutgoingTransferPreparedEvent}.
   *
   * @param event A {@link OutgoingTransferPreparedEvent}.
   */
  void onTransferPrepared(OutgoingTransferPreparedEvent event);

  /**
   * Called to handle an {@link OutgoingTransferFulfilledEvent}.
   *
   * @param event A {@link OutgoingTransferFulfilledEvent}.
   */
  void onTransferFulfilled(OutgoingTransferFulfilledEvent event);

  /**
   * Called to handle an {@link OutgoingTransferCancelledEvent}.
   *
   * @param event A {@link OutgoingTransferCancelledEvent}.
   */
  void onTransferCancelled(OutgoingTransferCancelledEvent event);

  /**
   * Called to handle an {@link OutgoingTransferRejectedEvent}.
   *
   * @param event A {@link OutgoingTransferRejectedEvent}.
   */
  void onTransferRejected(OutgoingTransferRejectedEvent event);

}
