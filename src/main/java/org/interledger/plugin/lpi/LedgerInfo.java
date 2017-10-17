package org.interledger.plugin.lpi;

import org.interledger.InterledgerAddress;

import org.immutables.value.Value;
import org.immutables.value.Value.Default;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import javax.money.CurrencyUnit;

/**
 * Metadata about an Interledger-enabled Ledger, as provided by the ledger or via configuration.
 */
@Value.Immutable
public interface LedgerInfo {

  /**
   * The ledger prefix for ledger that manages the "base" asset.
   *
   * @return {@link InterledgerAddress} containing the ledger prefix.
   */
  InterledgerAddress getLedgerPrefix();

  /**
   * The currency unit of the asset managed by the "base" ledger. Despite the name of the returned
   * object, this value may represent a non-currency asset).
   *
   * @return A {@link CurrencyUnit}.
   */
  CurrencyUnit getCurrencyUnit();

  /**
   * The number of digits before the decimal point (precision), supported by this peer.
   */
  Integer getCurrencyPrecision();

  /**
   * The number of digits after the decimal point (scale), supported by this peer.
   */
  Integer getCurrencyScale();

  /**
   * ILP addresses of connectors that have accounts on this ledger.
   */
  List<InterledgerAddress> getConnectorAddresses();

  /**
   * The minimum balance, zero by default.
   */
  @Default
  default BigInteger getMinBalance() {
    return BigInteger.ZERO;
  }

  /**
   * The maximum balance. Optional, defaults to plus infinity.
   */
  Optional<BigInteger> getMaxBalance();

  /**
   * Precondition enforcer that mandates the ledger prefix must actually be a ledger prefix, and not
   * just a regular Interledger Address.
   *
   * @see "https://github.com/interledger/rfcs/blob/master/0015-ilp-addresses/0015-ilp-addresses.md"
   */
  @Value.Check
  default void check() {
    InterledgerAddress.requireLedgerPrefix(getLedgerPrefix());
  }
}
