package org.interledger.plugin.lpi;

import org.interledger.InterledgerAddress;

import java.util.Map;
import java.util.Optional;

import javax.money.CurrencyUnit;

/**
 * Configuration information relating to a ledger plugin. This object is used to initialize a {@link
 * LedgerPlugin}, but not otherwise used in ledger plugin implementations.
 */
public interface LedgerPluginConfig {

  String LEDGER_PREFIX = "ledger_prefix";
  String LEDGER_PLUGIN_TYPE_ID = "ledger_plugin_type_id";
  String CONNECTOR_ACCOUNT = "connector_account";
  String EXPECTED_CURRENCY_UNIT = "expected_currency_unit";
  String TIMEOUT = "timeout";

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
   */
  Map<String, String> getOptions();

}
