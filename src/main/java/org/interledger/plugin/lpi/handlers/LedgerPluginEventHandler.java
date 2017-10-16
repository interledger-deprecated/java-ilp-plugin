package org.interledger.plugin.lpi.handlers;

/**
 * An interface that represents a ledger event handler that handles all available event types.
 */
public interface LedgerPluginEventHandler extends IncomingLedgerPluginTransferEventHandler,
    OutgoingLedgerPluginTransferEventHandler, LedgerPluginMessageEventHandler,
    LedgerPluginInfoEventHandler {



}
