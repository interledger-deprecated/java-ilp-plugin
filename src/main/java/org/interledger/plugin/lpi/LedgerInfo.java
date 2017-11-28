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
   * <p>The order of magnitude to express one full currency unit in ledger's base units. More
   * formally, an integer (..., -2, -1, 0, 1, 2, ...), such that one of the ledger's base units
   * equals 10^-<tt>currencyScale</tt> <tt>currencyCode</tt></p>
   *
   * <p>For example, if the integer values represented on the ledger are to be interpreted as
   * dollar-cents (for the purpose of settling a user's account balance, for instance), then the
   * ledger's currencyScale is 2. The amount 10000 would be translated visually into a decimal
   * format via the following equation: 10000 * (10^(-2)), or "100.00".</p>
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
    InterledgerAddress.requireAddressPrefix(getLedgerPrefix());
  }
}
