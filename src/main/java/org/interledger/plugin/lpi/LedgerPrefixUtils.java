package org.interledger.plugin.lpi;

import org.interledger.InterledgerAddress;

/**
 * @deprecated Remove this once https://github.com/interledger/java-ilp-core/pull/98 is released.
 */
@Deprecated
public interface LedgerPrefixUtils {

  /**
   * Asserts that {@code ledgerPrefix} is an Interledger ledger-prefix (i.e.,  the address ends with
   * a dot (.)).
   *
   * @param ledgerPrefix A {@link InterledgerAddress}.
   *
   * @throws IllegalArgumentException if the supplied Interledger address is not a ledger-prefix.
   * @deprecated TODO: Move to java-ilp-core
   */
  @Deprecated
  static InterledgerAddress assertLedgerPrefix(final InterledgerAddress ledgerPrefix) {
    if (ledgerPrefix.isLedgerPrefix() == false) {
      throw new IllegalArgumentException(
          String.format("InterledgerAddress MUST be a Ledger Prefix ending with a dot (.): %s",
              ledgerPrefix)
      );
    } else {
      return ledgerPrefix;
    }
  }

  /**
   * Asserts that {@code ledgerPrefix} is NOT an Interledger ledger-prefix (i.e., the address does
   * end with a dot (.)).
   *
   * @param ledgerPrefix A {@link InterledgerAddress}.
   *
   * @throws IllegalArgumentException if the supplied Interledger address is not a ledger-prefix.
   * @deprecated TODO: Move to java-ilp-core
   */
  @Deprecated
  static InterledgerAddress assertNotLedgerPrefix(final InterledgerAddress ledgerPrefix) {
    if (ledgerPrefix.isLedgerPrefix() == true) {
      throw new IllegalArgumentException(
          String.format("InterledgerAddress must NOT be a Ledger Prefix ending with a dot (.): %s",
              ledgerPrefix)
      );
    } else {
      return ledgerPrefix;
    }
  }

}
