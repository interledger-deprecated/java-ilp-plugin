package org.interledger.plugin.lpi.events;

/**
 * A parent interface for "outgoing" events, which signal information about activities that a ledger plugin and
 * connector should be made aware of relating to transfers and messages meant to flow from the connector (e.g ., a new
 * conditional transfer was prepared, with funds source from the connector, on an underlying ledger).
 */
public interface OutgoingLedgerPluginEvent extends LedgerPluginEvent {

}
