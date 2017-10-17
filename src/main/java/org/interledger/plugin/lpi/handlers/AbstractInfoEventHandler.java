package org.interledger.plugin.lpi.handlers;

import org.interledger.plugin.lpi.events.IncomingMessgeRequestEvent;
import org.interledger.plugin.lpi.events.IncomingTransferCancelledEvent;
import org.interledger.plugin.lpi.events.IncomingTransferFulfilledEvent;
import org.interledger.plugin.lpi.events.IncomingTransferPreparedEvent;
import org.interledger.plugin.lpi.events.IncomingTransferRejectedEvent;
import org.interledger.plugin.lpi.events.OutgoingMessgeRequestEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferCancelledEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferFulfilledEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferPreparedEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferRejectedEvent;


/**
 * An abstract implementation of {@link LedgerPluginEventHandler} that no-ops all methods except
 * those relating to {@link LedgerPluginInfoEventHandler}, which are left to the implementation to
 * define.
 */
public abstract class AbstractInfoEventHandler implements LedgerPluginEventHandler {

  @Override
  public void onTransferPrepared(IncomingTransferPreparedEvent event) {

  }

  @Override
  public void onTransferFulfilled(IncomingTransferFulfilledEvent event) {

  }

  @Override
  public void onTransferCancelled(IncomingTransferCancelledEvent event) {

  }

  @Override
  public void onTransferRejected(IncomingTransferRejectedEvent event) {

  }

  @Override
  public void onMessageRequest(IncomingMessgeRequestEvent event) {

  }

  @Override
  public void onMessageRequest(OutgoingMessgeRequestEvent event) {

  }

  @Override
  public void onTransferPrepared(OutgoingTransferPreparedEvent event) {

  }

  @Override
  public void onTransferFulfilled(OutgoingTransferFulfilledEvent event) {

  }

  @Override
  public void onTransferCancelled(OutgoingTransferCancelledEvent event) {

  }

  @Override
  public void onTransferRejected(OutgoingTransferRejectedEvent event) {

  }
}
