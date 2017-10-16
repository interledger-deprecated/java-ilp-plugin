package org.interledger.subprotocols;

import org.immutables.value.Value;

/**
 * Sub-protocol data that is encapsulated into a Binary Transfer Protocol (BTP) payload.
 *
 * @deprecated This will be merged into java-ilp-core. TODO
 */
@Deprecated
@Value.Immutable
public interface SubprotocolData {

  /**
   * The name of the protocol that this data represents.
   */
  String getProtocolName();

  /**
   * The content-type of the data payload.
   */
  String getDataContentType();

  /**
   * The data-payload for this particular sub-protocol data.
   */
  byte[] getData();
}
