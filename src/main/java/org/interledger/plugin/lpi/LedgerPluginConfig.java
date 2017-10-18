package org.interledger.plugin.lpi;

import org.interledger.InterledgerAddress;

import java.util.Map;
import java.util.Optional;

import javax.money.CurrencyUnit;

/**
 * Configuration information relating to a ledger plugin. This object is used to initialize a {@link
 * LedgerPlugin}, but not otherwise used in ledger plugin implementations.
 *
 * Additionally, the mechanism used to supply the typed values defined in this interface are
 * implementation defined, and will likely vary depending on each implementation's configuration
 * source. As an extension mechanism, {@link #getOptions()} is provided to allow arbitrary
 * configuration options, but the preferred mechanism is to extend this class and then provide typed
 * configuration to any particular ledger plugin.
 */
public interface LedgerPluginConfig {

  /**
   * The type of this ledger plugin.
   */
  LedgerPluginTypeId getLedgerPluginTypeId();

  /**
   * The identifying ledger prefix for this plugin.
   */
  InterledgerAddress getLedgerPrefix();

  /**
   * The connector account on the underlying ledger.
   */
  InterledgerAddress getConnectorAccount();

  /**
   * The expected currency-unit for this ledger plugin.
   */
  CurrencyUnit getExpectedCurrencyUnit();

  /**
   * The number of milliseconds that the plugin should spend trying to connect before giving up. If
   * {@link Optional#empty}, then there is no timeout.
   */
  default Optional<Integer> getTimeout() {
    return Optional.empty();
  }

  /**
   * The options for a given ledger plugin.
   *
   * @deprecated This method may go away in the future, in favor of a fully-typed configuration
   *     system.
   */
  @Deprecated
  Map<String, String> getOptions();

}
