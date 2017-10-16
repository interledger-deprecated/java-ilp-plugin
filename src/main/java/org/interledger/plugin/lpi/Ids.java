package org.interledger.plugin.lpi;

import org.immutables.value.Value;

import java.util.UUID;

/**
 * Wrapped immutable classes for providing type-safe identifiers.
 */
public class Ids {

  /**
   * Identifier for {@link Transfer}.
   */
  @Value.Immutable
  @Wrapped
  static abstract class _TransferId extends Wrapper<UUID> {

  }

  /**
   * Identifier for {@link Message}.
   */
  @Value.Immutable
  @Wrapped
  static abstract class _MessageId extends Wrapper<UUID> {

  }

  /**
   * A wrapper type that defines a "type" of ledger plugin based upon a unique String. For example,
   * "ilp-mock-plugin" or "btp-plugin".
   */
  @Value.Immutable
  @Wrapped
  static abstract class _LedgerPluginTypeId extends Wrapper<String> {

  }

}
