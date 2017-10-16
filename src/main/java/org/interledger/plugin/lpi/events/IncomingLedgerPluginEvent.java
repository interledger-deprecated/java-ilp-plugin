package org.interledger.plugin.lpi.events;

/**
 * A parent interface for "incoming" ledger plugin events, which signal information about activities
 * that a ledger plugin and connector should be made aware of relating to transfers and messages
 * meant to flow into the connector (e.g., a new conditional transfer was prepared on an underlying
 * ledger, destined for this connector).
 */
public interface IncomingLedgerPluginEvent extends LedgerPluginEvent {

}
