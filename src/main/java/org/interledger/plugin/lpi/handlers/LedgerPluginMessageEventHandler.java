package org.interledger.plugin.lpi.handlers;

import org.interledger.plugin.lpi.Response;
import org.interledger.plugin.lpi.events.IncomingMessgeRequestEvent;
import org.interledger.plugin.lpi.events.OutgoingMessgeRequestEvent;

public interface LedgerPluginMessageEventHandler  {

  /**
   * Called to handle an {@link IncomingMessgeRequestEvent}.
   *
   * @param event A {@link IncomingMessgeRequestEvent}.
   *
   * @return A {@link Response} acknowledgement returned from
   */
  void onMessageRequest(IncomingMessgeRequestEvent event);

  /**
   * Called to handle an {@link OutgoingMessgeRequestEvent}.
   *
   * @param event A {@link OutgoingMessgeRequestEvent}.
   */
  void onMessageRequest(OutgoingMessgeRequestEvent event);

  // TODO: Handle responses?
}
