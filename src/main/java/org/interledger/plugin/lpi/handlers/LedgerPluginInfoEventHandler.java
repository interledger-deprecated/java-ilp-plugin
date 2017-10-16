package org.interledger.plugin.lpi.handlers;

import org.interledger.plugin.lpi.LedgerPlugin;
import org.interledger.plugin.lpi.events.LedgerInfoChangedEvent;
import org.interledger.plugin.lpi.events.LedgerPluginConnectedEvent;
import org.interledger.plugin.lpi.events.LedgerPluginDisconnectedEvent;
import org.interledger.plugin.lpi.events.LedgerPluginErrorEvent;

/**
 * Handler interface that defines all events related to a {@link LedgerPlugin}.
 */
public interface LedgerPluginInfoEventHandler {

  /**
   * Called to handle an {@link LedgerInfoChangedEvent}.
   *
   * @param event A {@link LedgerInfoChangedEvent}.
   */
  void onLedgerInfoChanged(LedgerInfoChangedEvent event);

  /**
   * Called to handle an {@link LedgerPluginConnectedEvent}.
   *
   * @param event A {@link LedgerPluginConnectedEvent}.
   */
  void onConnect(LedgerPluginConnectedEvent event);

  /**
   * Called to handle an {@link LedgerPluginDisconnectedEvent}.
   *
   * @param event A {@link LedgerPluginDisconnectedEvent}.
   */
  void onDisconnect(LedgerPluginDisconnectedEvent event);

  /**
   * Called to handle an {@link LedgerPluginErrorEvent}.
   *
   * @param event A {@link LedgerPluginErrorEvent}.
   */
  void onError(LedgerPluginErrorEvent event);

}
