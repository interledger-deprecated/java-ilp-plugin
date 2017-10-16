package org.interledger.plugin.lpi;


import org.interledger.subprotocols.SubprotocolData;

import org.immutables.value.Value;

import java.util.List;
import java.util.UUID;

/**
 * A response object returned by a remote ledger plugin after sending a {@link Message} request.
 */
@Value.Immutable
public interface Response {

  UUID getRequestId();

  List<SubprotocolData> getSubProtocolData();
}
