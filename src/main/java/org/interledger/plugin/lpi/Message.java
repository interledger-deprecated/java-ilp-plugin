package org.interledger.plugin.lpi;

import org.interledger.InterledgerAddress;
import org.interledger.ilp.InterledgerPayment;
import org.interledger.subprotocols.SubprotocolData;

import org.immutables.value.Value;

import java.util.List;
import java.util.UUID;

/**
 * A representation of a message that can be sent to a local-ledger to accomplish some desired
 * behavior.
 */
@Value.Immutable
public interface Message {

  /**
   * A 128-bit {@link UUID} used as a unique message identifier
   */
  MessageId getId();

  /**
   * The ILP Address of the source account sending this message.
   */
  InterledgerAddress getFromAddress();

  /**
   * The ILP Address of the destination account that should receive this message.
   */
  InterledgerAddress getToAddress();

  /**
   * The ILP Address prefix of the ledger to send this message to.
   */
  InterledgerAddress getLedgerPrefix();

  /**
   * An associated ILP payment packet, as defined by IL-RFC-0003.
   */
  InterledgerPayment getInterlederPaymentPacket();

  /**
   * Sub-protocol data that travels along with this transfer.
   */
  List<SubprotocolData> getSubprotocolData();
}