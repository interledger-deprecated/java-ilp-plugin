package org.interledger.plugin.lpi;

import org.interledger.InterledgerAddress;
import org.interledger.cryptoconditions.Condition;
import org.interledger.ilp.InterledgerPayment;
import org.interledger.subprotocols.SubprotocolData;

import org.immutables.value.Value;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * A representation of a transfer on an ledger underlying a particular {@link LedgerPlugin}.
 */
@Value.Immutable
public interface Transfer {

  /**
   * A 128-bit {@link UUID} used as an external identifier
   */
  TransferId getTransferId();

  /**
   * The optionally-present source account that the amount indicated in {@link #getAmount()} is to
   * be debited from in order to prepare a transfer.
   *
   * Note that this field is optional because some implementations of {@link LedgerPlugin} represent
   * a ledger with only two accounts (a sender and receiver pair), so specifying those account
   * values may not be necessary, such as a ledger plugin that speaks BTP to a remote peer.
   */
  InterledgerAddress getSourceAccount();

  /**
   * Get the amount of this transfer, denominated in native ledger units for the ledger this
   * transfer resides in. <p> Note that this interface does not place any restriction on the sign of
   * this amount, but implementations are free to restrict this to a non-negative value if desired.
   *
   * @return A {@link BigInteger} that can be positive of negative.
   */
  BigInteger getAmount();

  /**
   * The optionally-present destination account that the amount indicated in {@link #getAmount()}
   * should be credited to if this transfer is executed. <p> Note that this field is optional
   * because some implementations of {@link LedgerPlugin} represent a ledger with only two accounts
   * (a sender and receiver pair), so specifying those account values may not be necessary, such as
   * a ledger plugin that speaks BTP to a remote peer.
   */
  InterledgerAddress getDestinationAccount();

  /**
   * ILP Address prefix of the ledger.
   */
  InterledgerAddress getLedgerPrefix();

  /**
   * An associated ILP payment packet, as defined by IL-RFC-0003.
   */
  InterledgerPayment getInterlederPaymentPacket();

  /**
   * The execution condition for this transfer request, which is the SHA-256 hash of a random,
   * pseudo-random or deterministically generated 32-byte (256-bit) integer preimage called a
   * fulfillment. The underlying ledger MUST hold the transfer until either this condition or {@link
   * #getCancellationCondition()} has been fulfilled or the {@link #getExpiresAt()} time has been
   * reached.
   */
  Condition getExecutionCondition();

  /**
   * The cancellation condition for this transfer request, which is the SHA-256 hash of a random,
   * pseudo-random or deterministically generated 32-byte (256-bit) integer preimage called a
   * fulfillment. The underlying ledger MUST hold the transfer until either this condition or {@link
   * #getExecutionCondition()} has been fulfilled or the {@link #getExpiresAt()} time has been
   * reached.
   */
  Optional<Condition> getCancellationCondition();

  /**
   * The expiration date/time of this request, in UTC.
   */
  Instant getExpiresAt();

  /**
   * Sub-protocol data that travels along with this transfer.
   */
  List<SubprotocolData> getSubprotocolData();
}
